package com.hws.travel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères.")
    private String titre;
    
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères.")
    private String description;
    
    @Positive(message = "Le nombre de jours doit être supérieur à 0.")
    @Max(value = 365, message = "Le nombre de jours ne peut pas dépasser 365.")
    private Integer nombreJours;
    
    private List<String> mobilites;
    private List<String> saisons;
    private List<String> pourQui;
    
    @Valid
    private List<GuideActiviteCreateDto> guideActivites;
    
    private List<Long> invitedUserIds;
}
