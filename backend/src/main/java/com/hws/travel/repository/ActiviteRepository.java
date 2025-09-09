package com.hws.travel.repository;

import com.hws.travel.entity.Activite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActiviteRepository extends JpaRepository<Activite, Long> {
    // Ajoute ici des méthodes personnalisées si besoin
}
