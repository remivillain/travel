package com.hws.travel.config;

import com.hws.travel.entity.*;
import com.hws.travel.entity.enums.ActiviteCategorie;
import com.hws.travel.repository.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ActiviteRepository activiteRepository;
    private final GuideRepository guideRepository;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
                          ActiviteRepository activiteRepository, GuideRepository guideRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.activiteRepository = activiteRepository;
        this.guideRepository = guideRepository;
    }

    @Override
    public void run(String... args) {
        // Créer les rôles
        Role adminRole = createRoleIfNotExists("ADMIN");
        Role userRole = createRoleIfNotExists("USER");

        // Créer les utilisateurs de test
        createUsersIfNotExists(adminRole, userRole);

        // Créer les activités de test
        createActivitiesIfNotExists();

        // Créer les guides de test
        createGuidesIfNotExists();
    }

    private Role createRoleIfNotExists(String roleName) {
        return roleRepository.findByName(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setName(roleName);
            return roleRepository.save(role);
        });
    }

    private void createUsersIfNotExists(Role adminRole, Role userRole) {
        // Utilisateur admin
        createUserIfNotExists("admin@admin.com", 
                             "$2a$12$PQEI0FMz1WoHTEEPbsGK2ekejnSHM3FkgfZKBshBAuycVWHXkv5Sq", // password: admin123
                             List.of(adminRole));

        // Utilisateurs de test
        createUserIfNotExists("user1@test.com",
                             "$2a$12$PQEI0FMz1WoHTEEPbsGK2ekejnSHM3FkgfZKBshBAuycVWHXkv5Sq", // password: admin123  
                             List.of(userRole));

        createUserIfNotExists("user2@test.com",
                             "$2a$12$PQEI0FMz1WoHTEEPbsGK2ekejnSHM3FkgfZKBshBAuycVWHXkv5Sq", // password: admin123
                             List.of(userRole));

        createUserIfNotExists("guide@test.com",
                             "$2a$12$PQEI0FMz1WoHTEEPbsGK2ekejnSHM3FkgfZKBshBAuycVWHXkv5Sq", // password: admin123
                             List.of(userRole));
    }

    private void createUserIfNotExists(String email, String password, List<Role> roles) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setRoles(new ArrayList<>(roles));
            userRepository.save(user);
        }
    }

    private void createActivitiesIfNotExists() {
        if (activiteRepository.count() == 0) {
            // Activités culturelles
            createActivity("Musée du Louvre", "Le plus grand musée d'art au monde", 
                          ActiviteCategorie.MUSEE, "Rue de Rivoli, 75001 Paris", 
                          "01.40.20.50.50", "9h-18h (fermé mardi)", "https://www.louvre.fr");

            createActivity("Château de Versailles", "Résidence royale emblématique", 
                          ActiviteCategorie.CHATEAU, "Place d'Armes, 78000 Versailles", 
                          "01.30.83.78.00", "9h-18h30", "https://www.chateauversailles.fr");

            createActivity("Musée d'Orsay", "Art impressionniste et post-impressionniste", 
                          ActiviteCategorie.MUSEE, "1 Rue de la Légion d'Honneur, 75007 Paris", 
                          "01.40.49.48.14", "9h30-18h (jeudi 21h45)", "https://www.musee-orsay.fr");

            // Activités nature
            createActivity("Parc des Buttes-Chaumont", "Parc paisible avec lac et belvédère", 
                          ActiviteCategorie.PARC, "1 Rue Botzaris, 75019 Paris", 
                          "01.48.03.83.10", "7h-22h (été), 7h-20h (hiver)", null);

            createActivity("Grottes de Lascaux IV", "Réplique de la grotte préhistorique", 
                          ActiviteCategorie.GROTTE, "Avenue de Lascaux, 24290 Montignac", 
                          "05.53.50.99.10", "9h-19h (été), 10h-18h (hiver)", "https://www.lascaux.fr");

            // Activités diverses
            createActivity("Tour Eiffel", "Monument emblématique de Paris", 
                          ActiviteCategorie.ACTIVITE, "Champ de Mars, 75007 Paris", 
                          "08.92.70.12.39", "9h30-23h45", "https://www.toureiffel.paris");

            createActivity("Château de Chambord", "Chef-d'œuvre de la Renaissance", 
                          ActiviteCategorie.CHATEAU, "41250 Chambord", 
                          "02.54.50.40.00", "9h-18h", "https://www.chambord.org");

            createActivity("Jardin du Luxembourg", "Jardin à la française au cœur de Paris", 
                          ActiviteCategorie.PARC, "15 Rue de Vaugirard, 75006 Paris", 
                          "01.42.34.20.00", "7h30-21h30 (été)", null);
        }
    }

    private void createActivity(String titre, String description, ActiviteCategorie categorie,
                               String adresse, String telephone, String horaires, String siteInternet) {
        Activite activite = new Activite();
        activite.setTitre(titre);
        activite.setDescription(description);
        activite.setCategorie(categorie);
        activite.setAdresse(adresse);
        activite.setTelephone(telephone);
        activite.setHorairesOuverture(horaires);
        activite.setSiteInternet(siteInternet);
        activiteRepository.save(activite);
    }

    private void createGuidesIfNotExists() {
        if (guideRepository.count() == 0) {
            // Guide Paris culturel
            Guide guideParis = createGuide("Paris Culturel - 3 jours", 
                                         "Découverte des principaux musées et monuments parisiens", 3);
            
            // Guide Loire châteaux  
            Guide guideLoire = createGuide("Châteaux de la Loire - 5 jours", 
                                         "Circuit découverte des plus beaux châteaux de la Loire", 5);

            // Guide Paris nature
            Guide guideNature = createGuide("Paris Vert - 2 jours", 
                                          "Les plus beaux parcs et jardins de Paris", 2);

            // Inviter quelques utilisateurs aux guides
            addInvitationsToGuides(guideParis, guideLoire, guideNature);
        }
    }

    private Guide createGuide(String titre, String description, int nombreJours) {
        Guide guide = new Guide();
        guide.setTitre(titre);
        guide.setDescription(description);
        guide.setNombreJours(nombreJours);
        guide.setInvitedUsers(new ArrayList<>());
        return guideRepository.save(guide);
    }

    private void addInvitationsToGuides(Guide guideParis, Guide guideLoire, Guide guideNature) {
        // Récupérer les utilisateurs de test
        User user1 = userRepository.findByEmail("user1@test.com").orElse(null);
        User user2 = userRepository.findByEmail("user2@test.com").orElse(null);
        User guideUser = userRepository.findByEmail("guide@test.com").orElse(null);

        if (user1 != null && user2 != null && guideUser != null) {
            // Guide Paris : accessible à user1 et guide@test.com
            guideParis.getInvitedUsers().add(user1);
            guideParis.getInvitedUsers().add(guideUser);
            guideRepository.save(guideParis);

            // Guide Loire : accessible à user2 et guide@test.com  
            guideLoire.getInvitedUsers().add(user2);
            guideLoire.getInvitedUsers().add(guideUser);
            guideRepository.save(guideLoire);

            // Guide Nature : accessible à tous les utilisateurs de test
            guideNature.getInvitedUsers().add(user1);
            guideNature.getInvitedUsers().add(user2);
            guideNature.getInvitedUsers().add(guideUser);
            guideRepository.save(guideNature);
        }
    }
}