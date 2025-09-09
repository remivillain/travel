package com.hws.travel.repository;

import com.hws.travel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Ajoute ici des méthodes personnalisées si besoin
}
