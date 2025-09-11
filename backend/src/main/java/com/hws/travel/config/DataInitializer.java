package com.hws.travel.config;

import com.hws.travel.entity.*;
import com.hws.travel.entity.enums.ActiviteCategorie;
import com.hws.travel.entity.enums.Mobilite;
import com.hws.travel.entity.enums.Saison;
import com.hws.travel.entity.enums.PourQui;
import com.hws.travel.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ActiviteRepository activiteRepository;
    private final GuideRepository guideRepository;
    private final GuideActiviteRepository guideActiviteRepository;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository,
                          ActiviteRepository activiteRepository, GuideRepository guideRepository,
                          GuideActiviteRepository guideActiviteRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.activiteRepository = activiteRepository;
        this.guideRepository = guideRepository;
        this.guideActiviteRepository = guideActiviteRepository;
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

        // Associer les activités aux guides
        addActivitiesToGuides();
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
            Guide guideParis = createGuideWithProperties("Paris Culturel", 
                                         "Découverte des principaux musées et monuments parisiens", 3,
                                         Arrays.asList(Mobilite.A_PIED, Mobilite.VOITURE),
                                         Arrays.asList(Saison.PRINTEMPS, Saison.ETE, Saison.AUTOMNE),
                                         Arrays.asList(PourQui.FAMILLE, PourQui.AMIS));
            
            // Guide Loire châteaux  
            Guide guideLoire = createGuideWithProperties("Châteaux de la Loire", 
                                         "Circuit découverte des plus beaux châteaux de la Loire", 5,
                                         Arrays.asList(Mobilite.VOITURE),
                                         Arrays.asList(Saison.PRINTEMPS, Saison.ETE),
                                         Arrays.asList(PourQui.FAMILLE, PourQui.GROUPE));

            // Guide Paris nature
            Guide guideNature = createGuideWithProperties("Paris Vert", 
                                          "Les plus beaux parcs et jardins de Paris", 2,
                                          Arrays.asList(Mobilite.A_PIED, Mobilite.VELO),
                                          Arrays.asList(Saison.PRINTEMPS, Saison.ETE),
                                          Arrays.asList(PourQui.FAMILLE, PourQui.SEUL));

            // Inviter quelques utilisateurs aux guides
            addInvitationsToGuides(guideParis, guideLoire, guideNature);
        }
    }

    private Guide createGuideWithProperties(String titre, String description, int nombreJours,
                                          List<Mobilite> mobilites, List<Saison> saisons, List<PourQui> pourQui) {
        Guide guide = new Guide();
        guide.setTitre(titre);
        guide.setDescription(description);
        guide.setNombreJours(nombreJours);
        guide.setInvitedUsers(new ArrayList<>());
        guide.setMobilites(mobilites);
        guide.setSaisons(saisons);
        guide.setPourQui(pourQui);
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

    private void addActivitiesToGuides() {
        if (guideActiviteRepository.count() == 0) {
            // Récupérer tous les guides et activités
            List<Guide> guides = guideRepository.findAll();
            List<Activite> activites = activiteRepository.findAll();

            // Trouver les guides par titre
            Guide guideParis = guides.stream()
                .filter(g -> "Paris Culturel".equals(g.getTitre()))
                .findFirst().orElse(null);
            Guide guideLoire = guides.stream()
                .filter(g -> "Châteaux de la Loire".equals(g.getTitre()))
                .findFirst().orElse(null);
            Guide guideNature = guides.stream()
                .filter(g -> "Paris Vert".equals(g.getTitre()))
                .findFirst().orElse(null);

            // Trouver les activités par titre
            Activite louvre = activites.stream()
                .filter(a -> "Musée du Louvre".equals(a.getTitre()))
                .findFirst().orElse(null);
            Activite versailles = activites.stream()
                .filter(a -> "Château de Versailles".equals(a.getTitre()))
                .findFirst().orElse(null);
            Activite orsay = activites.stream()
                .filter(a -> "Musée d'Orsay".equals(a.getTitre()))
                .findFirst().orElse(null);
            Activite eiffel = activites.stream()
                .filter(a -> "Tour Eiffel".equals(a.getTitre()))
                .findFirst().orElse(null);
            Activite chambord = activites.stream()
                .filter(a -> "Château de Chambord".equals(a.getTitre()))
                .findFirst().orElse(null);
            Activite lascaux = activites.stream()
                .filter(a -> "Grottes de Lascaux IV".equals(a.getTitre()))
                .findFirst().orElse(null);
            Activite buttes = activites.stream()
                .filter(a -> "Parc des Buttes-Chaumont".equals(a.getTitre()))
                .findFirst().orElse(null);
            Activite luxembourg = activites.stream()
                .filter(a -> "Jardin du Luxembourg".equals(a.getTitre()))
                .findFirst().orElse(null);

            List<GuideActivite> guideActivites = new ArrayList<>();

            // Guide Paris Culturel (3 jours)
            if (guideParis != null) {
                if (louvre != null) {
                    GuideActivite ga1 = new GuideActivite();
                    ga1.setGuide(guideParis);
                    ga1.setActivite(louvre);
                    ga1.setJour(1);
                    ga1.setOrdre(1);
                    guideActivites.add(ga1);
                }
                if (eiffel != null) {
                    GuideActivite ga2 = new GuideActivite();
                    ga2.setGuide(guideParis);
                    ga2.setActivite(eiffel);
                    ga2.setJour(1);
                    ga2.setOrdre(2);
                    guideActivites.add(ga2);
                }
                if (orsay != null) {
                    GuideActivite ga3 = new GuideActivite();
                    ga3.setGuide(guideParis);
                    ga3.setActivite(orsay);
                    ga3.setJour(2);
                    ga3.setOrdre(1);
                    guideActivites.add(ga3);
                }
                if (versailles != null) {
                    GuideActivite ga4 = new GuideActivite();
                    ga4.setGuide(guideParis);
                    ga4.setActivite(versailles);
                    ga4.setJour(3);
                    ga4.setOrdre(1);
                    guideActivites.add(ga4);
                }
            }

            // Guide Châteaux de la Loire (5 jours)
            if (guideLoire != null) {
                if (chambord != null) {
                    GuideActivite ga5 = new GuideActivite();
                    ga5.setGuide(guideLoire);
                    ga5.setActivite(chambord);
                    ga5.setJour(1);
                    ga5.setOrdre(1);
                    guideActivites.add(ga5);
                }
                if (versailles != null) {
                    GuideActivite ga6 = new GuideActivite();
                    ga6.setGuide(guideLoire);
                    ga6.setActivite(versailles);
                    ga6.setJour(2);
                    ga6.setOrdre(1);
                    guideActivites.add(ga6);
                }
                if (lascaux != null) {
                    GuideActivite ga7 = new GuideActivite();
                    ga7.setGuide(guideLoire);
                    ga7.setActivite(lascaux);
                    ga7.setJour(3);
                    ga7.setOrdre(1);
                    guideActivites.add(ga7);
                }
            }

            // Guide Paris Vert (2 jours)
            if (guideNature != null) {
                if (buttes != null) {
                    GuideActivite ga8 = new GuideActivite();
                    ga8.setGuide(guideNature);
                    ga8.setActivite(buttes);
                    ga8.setJour(1);
                    ga8.setOrdre(1);
                    guideActivites.add(ga8);
                }
                if (luxembourg != null) {
                    GuideActivite ga9 = new GuideActivite();
                    ga9.setGuide(guideNature);
                    ga9.setActivite(luxembourg);
                    ga9.setJour(1);
                    ga9.setOrdre(2);
                    guideActivites.add(ga9);
                }
                if (eiffel != null) {
                    GuideActivite ga10 = new GuideActivite();
                    ga10.setGuide(guideNature);
                    ga10.setActivite(eiffel);
                    ga10.setJour(2);
                    ga10.setOrdre(1);
                    guideActivites.add(ga10);
                }
            }

            // Sauvegarder toutes les associations
            if (!guideActivites.isEmpty()) {
                guideActiviteRepository.saveAll(guideActivites);
            }
        }
    }
}