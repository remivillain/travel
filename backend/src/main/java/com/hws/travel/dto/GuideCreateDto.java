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
public class GuideCreateDto {
    @NotBlank(message = "Le titre est obligatoire.")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères.")
    private String titre;
    
    @NotBlank(message = "La description est obligatoire.")
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères.")
    private String description;
    
    @Positive(message = "Le nombre de jours doit être supérieur à 0.")
    @Max(value = 365, message = "Le nombre de jours ne peut pas dépasser 365.")
    private int nombreJours;
    
    @NotEmpty(message = "Au moins une mobilité est requise.")
    private List<String> mobilites;
    
    @NotEmpty(message = "Au moins une saison est requise.")
    private List<String> saisons;
    
    @NotEmpty(message = "Au moins une option 'pour qui' est requise.")
    private List<String> pourQui;
    
    @Valid
    private List<GuideActiviteCreateDto> guideActivites;
    
    private List<Long> invitedUserIds;
}
