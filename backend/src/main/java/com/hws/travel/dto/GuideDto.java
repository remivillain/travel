
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
public class GuideDto {
    private Long id;
    private String titre;
    private String description;
    private int nombreJours;
    private List<String> mobilites;
    private List<String> saisons;
    private List<String> pourQui;
    private List<GuideActiviteDto> guideActivites;
    private List<Long> invitedUserIds;
}
