package com.hws.travel.mapper;

import com.hws.travel.entity.Guide;
import com.hws.travel.entity.User;
import com.hws.travel.entity.enums.Mobilite;
import com.hws.travel.entity.enums.PourQui;
import com.hws.travel.entity.enums.Saison;
import com.hws.travel.dto.GuideDto;


public class GuideMapper {
    private GuideMapper() {}

    public static GuideDto toDto(Guide guide) {
        if (guide == null) return null;

        return GuideDto.builder()
            .id(guide.getId())
            .titre(guide.getTitre())
            .description(guide.getDescription())
            .nombreJours(guide.getNombreJours())
            .mobilites(guide.getMobilites() != null
                ? guide.getMobilites().stream().map(Enum::name).toList()
                : null)
            .saisons(guide.getSaisons() != null
                ? guide.getSaisons().stream().map(Enum::name).toList()
                : null)
            .pourQui(guide.getPourQui() != null
                ? guide.getPourQui().stream().map(Enum::name).toList()
                : null)

            // Utilise le mapping complet pour inclure l'activité dans chaque GuideActiviteDto
            .guideActivites(guide.getGuideActivites() != null
                ? guide.getGuideActivites().stream()
                    .map(GuideActiviteMapper::toDto)
                    .toList()
                : null)

            // ne mapper que les IDs des users invités
            .invitedUserIds(guide.getInvitedUsers() != null
                ? guide.getInvitedUsers().stream().map(User::getId).toList()
                : null)
            .build();
    }

    public static Guide toEntity(GuideDto dto) {
        if (dto == null) return null;

        Guide guide = new Guide();
        guide.setId(dto.getId());
        guide.setTitre(dto.getTitre());
        guide.setDescription(dto.getDescription());
        guide.setNombreJours(dto.getNombreJours());

        if (dto.getMobilites() != null) {
            guide.setMobilites(dto.getMobilites().stream().map(Mobilite::valueOf).toList());
        }
        if (dto.getSaisons() != null) {
            guide.setSaisons(dto.getSaisons().stream().map(Saison::valueOf).toList());
        }
        if (dto.getPourQui() != null) {
            guide.setPourQui(dto.getPourQui().stream().map(PourQui::valueOf).toList());
        }

        // guideActivites et invitedUsers -> gérés dans le service
        return guide;
    }
}
