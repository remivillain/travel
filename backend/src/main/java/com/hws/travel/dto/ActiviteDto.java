
package com.hws.travel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiviteDto {
    private Long id;
    private String titre;
    private String description;
    private String categorie;
    private String adresse;
    private String telephone;
    private String horairesOuverture;
    private String siteInternet;
    // jour, ordre, guideId sont gérés par GuideActivite
}
