package com.hws.travel.repository;

import com.hws.travel.entity.Guide;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideRepository extends JpaRepository<Guide, Long> {
    // Ajoute ici des méthodes personnalisées si besoin
}
