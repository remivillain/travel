package com.hws.travel.mapper;

import com.hws.travel.entity.Activite;
import com.hws.travel.dto.ActiviteDto;
import com.hws.travel.entity.enums.ActiviteCategorie;

public class ActiviteMapper {
    private ActiviteMapper() {}

    public static ActiviteDto toDto(Activite activite) {
        if (activite == null) return null;
        return ActiviteDto.builder()
                .id(activite.getId())
                .titre(activite.getTitre())
                .description(activite.getDescription())
                .categorie(activite.getCategorie() != null ? activite.getCategorie().name() : null)
                .adresse(activite.getAdresse())
                .telephone(activite.getTelephone())
                .horairesOuverture(activite.getHorairesOuverture())
                .siteInternet(activite.getSiteInternet())
                .jour(activite.getJour())
                .ordre(activite.getOrdre())
                .guideId(activite.getGuide() != null ? activite.getGuide().getId() : null)
                .build();
    }

    public static Activite toEntity(ActiviteDto dto) {
        if (dto == null) return null;
        Activite activite = new Activite();
        activite.setId(dto.getId());
        activite.setTitre(dto.getTitre());
        activite.setDescription(dto.getDescription());
        // Pour la catégorie, il faut la convertir en enum
        if (dto.getCategorie() != null) {
            try {
                activite.setCategorie(ActiviteCategorie.valueOf(dto.getCategorie()));
            } catch (IllegalArgumentException e) {
                activite.setCategorie(null);
            }
        }
        activite.setAdresse(dto.getAdresse());
        activite.setTelephone(dto.getTelephone());
        activite.setHorairesOuverture(dto.getHorairesOuverture());
        activite.setSiteInternet(dto.getSiteInternet());
        activite.setJour(dto.getJour());
        activite.setOrdre(dto.getOrdre());
        // Pour guideId, il faudra gérer l'association dans le service
        return activite;
    }
}
