package com.hws.travel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuideUpdateDto {
    private String titre;
    private String description;
    private Integer nombreJours;
    private List<String> mobilites;
    private List<String> saisons;
    private List<String> pourQui;
    private List<GuideActiviteCreateDto> guideActivites;
    private List<Long> invitedUserIds;
}
