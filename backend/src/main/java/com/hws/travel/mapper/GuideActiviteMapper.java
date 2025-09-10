package com.hws.travel.mapper;

import com.hws.travel.entity.GuideActivite;
import com.hws.travel.dto.GuideActiviteDto;

public class GuideActiviteMapper {
    private GuideActiviteMapper() {}

    public static GuideActiviteDto toDto(GuideActivite entity) {
        if (entity == null) return null;
        return GuideActiviteDto.builder()
            .id(entity.getId())
            .guideId(entity.getGuide() != null ? entity.getGuide().getId() : null)
            .activiteId(entity.getActivite() != null ? entity.getActivite().getId() : null)
            .jour(entity.getJour())
            .ordre(entity.getOrdre())
            .activite(entity.getActivite() != null ? ActiviteMapper.toDto(entity.getActivite()) : null)
            .build();
    }

    // Optionnel : toEntity si besoin
}
