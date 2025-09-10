package com.hws.travel.config;
import com.hws.travel.entity.Role;
import com.hws.travel.entity.User;
import com.hws.travel.repository.RoleRepository;
import com.hws.travel.repository.UserRepository;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        if (adminRole == null) {
            adminRole = roleRepository.save(new Role(null, "ADMIN", null));
        }
        if (roleRepository.findByName("USER").isEmpty()) {
            roleRepository.save(new Role(null, "USER", null));
        }

        // Création de l'utilisateur admin si absent
        String adminEmail = "admin@admin.com";
        String adminPassword = "$2a$12$PQEI0FMz1WoHTEEPbsGK2ekejnSHM3FkgfZKBshBAuycVWHXkv5Sq"; // Mot de passe hashé BCrypt
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(adminPassword);
            admin.setRoles(List.of(adminRole));
            userRepository.save(admin);
        }
    }
}