package com.hws.travel.repository;

import com.hws.travel.entity.GuideActivite;
import com.hws.travel.entity.Guide;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideActiviteRepository extends JpaRepository<GuideActivite, Long> {
    void deleteByGuide(Guide guide);
}
