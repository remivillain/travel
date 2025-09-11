
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
public class ActiviteDto {
    private Long id;
    
    @NotBlank(message = "Le titre de l'activité est obligatoire.")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères.")
    private String titre;
    
    @NotBlank(message = "La description de l'activité est obligatoire.")
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères.")
    private String description;
    
    @NotBlank(message = "La catégorie de l'activité est obligatoire.")
    private String categorie;
    
    @NotBlank(message = "L'adresse de l'activité est obligatoire.")
    @Size(max = 500, message = "L'adresse ne peut pas dépasser 500 caractères.")
    private String adresse;
    
    @NotBlank(message = "Le téléphone de l'activité est obligatoire.")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{8,20}$", message = "Le format du téléphone n'est pas valide.")
    private String telephone;
    
    @NotBlank(message = "Les horaires d'ouverture de l'activité sont obligatoires.")
    @Size(max = 255, message = "Les horaires d'ouverture ne peuvent pas dépasser 255 caractères.")
    private String horairesOuverture;
    
    @Size(max = 255, message = "L'URL du site internet ne peut pas dépasser 255 caractères.")
    @Pattern(regexp = "^(https?://)?(www\\.)?[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,}(/.*)?$", message = "Le format de l'URL n'est pas valide.", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String siteInternet;
    // jour, ordre, guideId sont gérés par GuideActivite
}
