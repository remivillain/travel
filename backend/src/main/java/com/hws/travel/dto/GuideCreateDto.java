package com.hws.travel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuideCreateDto {
    private String titre;
    private String description;
    private int nombreJours;
    private Set<String> mobilites;
    private Set<String> saisons;
    private Set<String> pourQui;
    private Set<GuideActiviteCreateDto> guideActivites;
    private Set<Long> invitedUserIds;
}
