package com.hws.travel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuideActiviteDto {
    private Long id;
    private Long guideId;
    private Long activiteId;
    private int jour;
    private int ordre;
    // Optionnel : inclure l'ActiviteDto pour faciliter l'affichage
    private ActiviteDto activite;
}
