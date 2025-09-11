package com.hws.travel.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuideActiviteCreateDto {
    @NotNull(message = "L'ID de l'activité est obligatoire.")
    @Positive(message = "L'ID de l'activité doit être positif.")
    private Long activiteId;
    
    @Positive(message = "Le jour doit être supérieur à 0.")
    @Max(value = 365, message = "Le jour ne peut pas dépasser 365.")
    private int jour;
    
    @Positive(message = "L'ordre doit être supérieur à 0.")
    @Max(value = 100, message = "L'ordre ne peut pas dépasser 100.")
    private int ordre;
}
