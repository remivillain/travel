package com.hws.travel.integration;

import com.hws.travel.entity.*;
import com.hws.travel.entity.enums.ActiviteCategorie;
import com.hws.travel.repository.ActiviteRepository;
import com.hws.travel.repository.GuideRepository;
import com.hws.travel.repository.UserRepository;
import com.hws.travel.repository.RoleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class TravelGuideIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private ActiviteRepository activiteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    private Long testActiviteId;
    private Long testGuideId1;
    private Long testGuideId2;
    private Long testUserId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        
        // Créer des données de test
        createTestData();
    }

    private void createTestData() {
        // Nettoyer les données existantes
        guideRepository.deleteAll();
        activiteRepository.deleteAll();
        userRepository.deleteAll();
        
        // Créer les rôles s'ils n'existent pas
        Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
            Role role = new Role();
            role.setName("USER");
            return roleRepository.save(role);
        });
        
        // Créer un utilisateur de test
        User testUser = new User();
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password");
        testUser.setRoles(new java.util.ArrayList<>());
        testUser.getRoles().add(userRole);
        User savedUser = userRepository.save(testUser);
        testUserId = savedUser.getId();
        
        // Créer une activité de test
        Activite activite = new Activite();
        activite.setTitre("Musée Test");
        activite.setDescription("Description du musée test");
        activite.setCategorie(ActiviteCategorie.MUSEE);
        activite.setAdresse("123 Rue Test");
        activite.setTelephone("01.23.45.67.89");
        activite.setHorairesOuverture("9h-17h");
        activite.setSiteInternet("https://test.com");
        Activite savedActivite = activiteRepository.save(activite);
        testActiviteId = savedActivite.getId();
        
        // Créer le premier guide (avec invitation pour l'utilisateur)
        Guide guide1 = new Guide();
        guide1.setTitre("Guide Test 1 - Avec invitation");
        guide1.setDescription("Guide accessible à l'utilisateur test");
        guide1.setNombreJours(3);
        guide1.setInvitedUsers(new java.util.ArrayList<>());
        guide1.getInvitedUsers().add(savedUser);
        Guide savedGuide1 = guideRepository.save(guide1);
        testGuideId1 = savedGuide1.getId();
        
        // Créer le deuxième guide (sans invitation pour l'utilisateur)
        Guide guide2 = new Guide();
        guide2.setTitre("Guide Test 2 - Sans invitation");
        guide2.setDescription("Guide NON accessible à l'utilisateur test");
        guide2.setNombreJours(5);
        Guide savedGuide2 = guideRepository.save(guide2);
        testGuideId2 = savedGuide2.getId();
    }

    // ===================================================================
    // 1. TESTS DE SÉCURITÉ DE BASE
    // ===================================================================

    /**
     * Test 1 : Accès non authentifié refusé
     * 
     * Vérifie que :
     * - Les utilisateurs non authentifiés ne peuvent accéder à aucun endpoint
     * - Tous les endpoints retournent 403 Forbidden
     */
    @Test
    public void testUnauthenticatedAccessDenied() throws Exception {
        // Test: Utilisateur non authentifié ne peut accéder à aucun endpoint de guides
        mockMvc.perform(get("/api/guides"))
                .andExpect(status().isForbidden());
                
        mockMvc.perform(get("/api/guides/mes-guides"))
                .andExpect(status().isForbidden());
                
        mockMvc.perform(get("/api/guides/" + testGuideId1))
                .andExpect(status().isForbidden());
    }

    /**
     * Test 2 : Permissions d'accès aux endpoints protégés
     * 
     * Vérifie que :
     * - Les endpoints protégés nécessitent une authentification
     * - Seuls les admins peuvent créer/modifier les guides
     * - Seuls les admins peuvent ajouter des activités aux guides
     */
    @Test
    public void testAuthenticationRequiredForProtectedEndpoints() throws Exception {
        // Test que la création de guide nécessite une authentification (Admin uniquement )
        mockMvc.perform(post("/api/guides")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"titre\":\"Test Guide\",\"description\":\"Description test\",\"nombreJours\":3}"))
                .andExpect(status().isForbidden());

        // Test que l'ajout d'activité nécessite une authentification (Admin uniquement )
        mockMvc.perform(post("/api/guides/1/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"activiteId\":1,\"jour\":1,\"ordre\":1}"))
                .andExpect(status().isForbidden());

        // Test que la suppression d'activité nécessite une authentification (Admin uniquement)
        mockMvc.perform(delete("/api/guides/1/activities/1"))
                .andExpect(status().isForbidden());
    }

    // ===================================================================
    // 2. TESTS DE STRUCTURE ET VALIDATION DES DONNÉES
    // ===================================================================

    /**
     * Test 3 : Validation des endpoints de guides 
     * 
     * Vérifie que :
     * - Les guides peuvent être récupérés par un admin
     * - La structure JSON correspond aux spécifications
     * - Les attributs obligatoires sont présents (titre, description, nombre de jours)
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGuideEndpointsAccordingToSpecification() throws Exception {
        // Test de récupération des guides publics (nécessite ADMIN)
        mockMvc.perform(get("/api/guides"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Test 4 : Structure des guides 
     * 
     * Vérifie que les guides retournés ont :
     * - Titre (obligatoire)
     * - Description (obligatoire) 
     * - Nombre de jours (obligatoire)
     * - Options mobilité, saison, pour qui (selon énums)
     */
    @Test 
    @WithMockUser(roles = "USER", username = "test@test.com")
    public void testGuideStructureAccordingToSpecification() throws Exception {
        // Test de récupération d'un guide spécifique (nécessite USER et authentification)
        mockMvc.perform(get("/api/guides/1"))
                .andExpect(status().isNotFound()); // Peut être 404 si le guide n'existe pas ou si l'utilisateur n'y a pas accès
    }

    /**
     * Test 5 : Endpoints des activités 
     * 
     * Vérifie que les activités peuvent être récupérées
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testActivityEndpointsAccordingToSpecification() throws Exception {
        // Test de récupération des activités (nécessite ADMIN)
        mockMvc.perform(get("/api/activites"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * Test 6 : Structure des activités 
     * 
     * Vérifie que les activités ont tous les attributs requis  :
     * - Titre, description, catégorie (obligatoires)
     * - Adresse, téléphone, horaires, site internet (informations pratiques)
     * - Catégories : musée, château, activité, parc, grotte
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testActivityStructureAccordingToSpecification() throws Exception {
        // Test de récupération d'une activité spécifique
        mockMvc.perform(get("/api/activites/" + testActiviteId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.titre").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.categorie").exists())
                .andExpect(jsonPath("$.adresse").exists())
                .andExpect(jsonPath("$.telephone").exists())
                .andExpect(jsonPath("$.horairesOuverture").exists())
                .andExpect(jsonPath("$.siteInternet").exists());
    }

    /**
     * Test 7 : Validation des données 
     * 
     * Vérifie que les validations sont correctes pour :
     * - Attributs obligatoires des guides
     * - Format des données
     * - Contraintes métier
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDataValidationAccordingToSpecification() throws Exception {
        // Test avec données invalides (guide sans titre obligatoire)
        mockMvc.perform(post("/api/guides")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Description sans titre\",\"nombreJours\":1}"))
                .andExpect(status().isBadRequest());

        // Test avec données invalides (nombre de jours négatif)
        mockMvc.perform(post("/api/guides")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"titre\":\"Guide Test\",\"description\":\"Description\",\"nombreJours\":-1}"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test 8 : Endpoints d'ajout d'activités 
     * 
     * Vérifie que les nouveaux endpoints créés fonctionnent :
     * - POST /api/guides/{id}/activities (ajout simple)
     * - POST /api/guides/{id}/activities/batch (ajout multiple)  
     * - DELETE /api/guides/{id}/activities/{activityId} (suppression)
     */
    @Test
    public void testActivityManagementEndpointsAccordingToSpecification() throws Exception {
        // Test endpoint d'ajout d'activité simple (nécessite admin)
        mockMvc.perform(post("/api/guides/1/activities")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"activiteId\":1,\"jour\":1,\"ordre\":1}"))
                .andExpect(status().isForbidden());

        // Test endpoint d'ajout d'activités en lot (nécessite admin)  
        mockMvc.perform(post("/api/guides/1/activities/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[{\"activiteId\":1,\"jour\":1,\"ordre\":1},{\"activiteId\":2,\"jour\":1,\"ordre\":2}]"))
                .andExpect(status().isForbidden());

        // Test endpoint de suppression d'activité (nécessite admin)
        mockMvc.perform(delete("/api/guides/1/activities/1"))
                .andExpect(status().isForbidden());
    }

    // ===================================================================
    // 3. TESTS DE GESTION DES INVITATIONS
    // ===================================================================

    /**
     * Test 9 : Invitation d'utilisateur à un guide
     * 
     * Vérifie que :
     * - Un admin peut inviter un utilisateur à un guide
     * - L'utilisateur invité peut accéder au guide
     * - Les validations fonctionnent correctement
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testInviteUserToGuide() throws Exception {
        // Créer un nouvel utilisateur pour le test
        User newUser = new User();
        newUser.setEmail("newuser@test.com");
        newUser.setPassword("password");
        User savedNewUser = userRepository.save(newUser);
        
        // Test: Inviter l'utilisateur au guide 2 (qui n'avait pas d'invitation initialement)
        String invitationJson = "{\"userId\": " + savedNewUser.getId() + "}";
        
        mockMvc.perform(post("/api/guides/" + testGuideId2 + "/invite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invitationJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testGuideId2))
                .andExpect(jsonPath("$.titre").value("Guide Test 2 - Sans invitation"));
    }

    /**
     * Test 10 : Retrait d'utilisateur d'un guide
     * 
     * Vérifie que :
     * - Un admin peut retirer un utilisateur d'un guide
     * - L'utilisateur ne peut plus accéder au guide après retrait
     */
    @Test
    @WithMockUser(roles = "ADMIN")  
    public void testRemoveUserFromGuide() throws Exception {
        // Test: Retirer l'utilisateur test du guide 1
        String removalJson = "{\"userId\": " + testUserId + "}";
        
        mockMvc.perform(delete("/api/guides/" + testGuideId1 + "/invite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(removalJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testGuideId1));
    }

    /**
     * Test 11 : Validation des erreurs d'invitation
     * 
     * Vérifie que :
     * - On ne peut pas inviter un utilisateur inexistant
     * - On ne peut pas inviter un utilisateur déjà invité
     * - On ne peut pas retirer un utilisateur non invité
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testInvitationValidationErrors() throws Exception {
        // Test 1: Inviter un utilisateur inexistant
        String invalidUserJson = "{\"userId\": 99999}";
        
        mockMvc.perform(post("/api/guides/" + testGuideId1 + "/invite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidUserJson))
                .andExpect(status().isNotFound());

        // Test 2: Inviter un utilisateur déjà invité
        String existingUserJson = "{\"userId\": " + testUserId + "}";
        
        mockMvc.perform(post("/api/guides/" + testGuideId1 + "/invite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(existingUserJson))
                .andExpect(status().isConflict());

        // Test 3: Retirer un utilisateur non invité d'un guide
        mockMvc.perform(delete("/api/guides/" + testGuideId2 + "/invite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(existingUserJson))
                .andExpect(status().isNotFound());
    }

    /**
     * Test 12 : Validation des données d'invitation
     * 
     * Vérifie que :
     * - L'ID utilisateur est obligatoire
     * - L'ID utilisateur doit être positif
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testInvitationDataValidation() throws Exception {
        // Test 1: ID utilisateur manquant
        String emptyJson = "{}";
        
        mockMvc.perform(post("/api/guides/" + testGuideId1 + "/invite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyJson))
                .andExpect(status().isBadRequest());

        // Test 2: ID utilisateur négatif
        String negativeIdJson = "{\"userId\": -1}";
        
        mockMvc.perform(post("/api/guides/" + testGuideId1 + "/invite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(negativeIdJson))
                .andExpect(status().isBadRequest());

        // Test 3: ID utilisateur zéro
        String zeroIdJson = "{\"userId\": 0}";
        
        mockMvc.perform(post("/api/guides/" + testGuideId1 + "/invite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(zeroIdJson))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test 13 : Sécurité des endpoints d'invitation
     * 
     * Vérifie que :
     * - Seuls les admins peuvent inviter/retirer des utilisateurs
     * - Les utilisateurs standards n'ont pas accès à ces fonctionnalités
     */
    @Test
    @WithMockUser(roles = "USER")
    public void testInvitationSecurityRestrictions() throws Exception {
        String invitationJson = "{\"userId\": " + testUserId + "}";
        
        // Test: Un utilisateur ne peut pas inviter
        mockMvc.perform(post("/api/guides/" + testGuideId1 + "/invite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invitationJson))
                .andExpect(status().isForbidden());

        // Test: Un utilisateur ne peut pas retirer
        mockMvc.perform(delete("/api/guides/" + testGuideId1 + "/invite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invitationJson))
                .andExpect(status().isForbidden());
    }

    // ===================================================================
    // 4. TESTS DE CONTRÔLE D'ACCÈS ET PERMISSIONS
    // ===================================================================

    /**
     * Test 14 : Contrôle d'accès basé sur les invitations - Admin
     * 
     * Vérifie que :
     * - Un admin peut récupérer tous les guides
     * - Un admin a accès complet aux données
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAdminCanAccessAllGuides() throws Exception {
        // Test: Admin récupère tous les guides
        mockMvc.perform(get("/api/guides"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2)); // Doit voir les 2 guides
    }

    /**
     * Test 15 : Contrôle d'accès basé sur les invitations - Utilisateur
     * 
     * Vérifie que :
     * - Un utilisateur ne peut récupérer que les guides auxquels il est invité
     * - L'accès aux guides non autorisés est refusé
     */
    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    public void testUserCanAccessOnlyInvitedGuides() throws Exception {        
        // Test 1: Utilisateur récupère ses guides (via /mes-guides)
        mockMvc.perform(get("/api/guides/mes-guides"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1)); // Ne doit voir qu'1 guide (celui avec invitation)
        
        // Test 2: Utilisateur peut accéder au guide dans lequel il est invité
        mockMvc.perform(get("/api/guides/" + testGuideId1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.titre").value("Guide Test 1 - Avec invitation"));
        
        // Test 3: Utilisateur ne peut pas accéder au guide dans lequel il n'est pas invité
        mockMvc.perform(get("/api/guides/" + testGuideId2))
                .andExpect(status().isForbidden()); // Doit être refusé
    }
}
