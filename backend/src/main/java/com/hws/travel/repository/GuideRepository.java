package com.hws.travel.repository;

import com.hws.travel.entity.Guide;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuideRepository extends JpaRepository<Guide, Long> {
    // Récupère les guides où un user est invité
    @Query("SELECT g FROM Guide g JOIN g.invitedUsers u WHERE u.id = :userId")
    List<Guide> findGuidesByInvitedUserId(@Param("userId") Long userId);
}
