package com.hws.travel.mapper;

import com.hws.travel.entity.Guide;
import com.hws.travel.entity.User;
import com.hws.travel.entity.enums.Mobilite;
import com.hws.travel.entity.enums.PourQui;
import com.hws.travel.entity.enums.Saison;
import com.hws.travel.dto.GuideDto;
import java.util.stream.Collectors;

public class GuideMapper {
    private GuideMapper() {}

    public static GuideDto toDto(Guide guide) {
        if (guide == null) return null;
        return GuideDto.builder()
            .id(guide.getId())
            .titre(guide.getTitre())
            .description(guide.getDescription())
            .nombreJours(guide.getNombreJours())
            .mobilites(guide.getMobilites() != null ? guide.getMobilites().stream().map(Enum::name).collect(Collectors.toSet()) : null)
            .saisons(guide.getSaisons() != null ? guide.getSaisons().stream().map(Enum::name).collect(Collectors.toSet()) : null)
            .pourQui(guide.getPourQui() != null ? guide.getPourQui().stream().map(Enum::name).collect(Collectors.toSet()) : null)
            .guideActivites(guide.getGuideActivites() != null ? guide.getGuideActivites().stream().map(GuideActiviteMapper::toDto).collect(Collectors.toSet()) : null)
            .invitedUserIds(guide.getInvitedUsers() != null ? guide.getInvitedUsers().stream().map(User::getId).collect(Collectors.toSet()) : null)
            .build();
    }

    public static Guide toEntity(GuideDto dto) {
        if (dto == null) return null;
        Guide guide = new Guide();
        guide.setId(dto.getId());
        guide.setTitre(dto.getTitre());
        guide.setDescription(dto.getDescription());
        guide.setNombreJours(dto.getNombreJours());
        // Pour les enums, il faut convertir les String en enum
        if (dto.getMobilites() != null)
            guide.setMobilites(dto.getMobilites().stream().map(Mobilite::valueOf).collect(Collectors.toSet()));
        if (dto.getSaisons() != null)
            guide.setSaisons(dto.getSaisons().stream().map(Saison::valueOf).collect(Collectors.toSet()));
        if (dto.getPourQui() != null)
            guide.setPourQui(dto.getPourQui().stream().map(PourQui::valueOf).collect(Collectors.toSet()));
        // Pour les activités et users, il faudra gérer l'association dans le service
        return guide;
    }
}
