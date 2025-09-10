package com.hws.travel.mapper;

import com.hws.travel.entity.GuideActivite;
import com.hws.travel.dto.GuideActiviteDto;

public class GuideActiviteMapper {
    private GuideActiviteMapper() {}

    // version complète (si besoin ailleurs, mais attention aux cycles)
    public static GuideActiviteDto toDto(GuideActivite entity) {
        if (entity == null) return null;
        return GuideActiviteDto.builder()
            .id(entity.getId())
            .jour(entity.getJour())
            .ordre(entity.getOrdre())
            .activiteId(entity.getActivite() != null ? entity.getActivite().getId() : null)
            .guideId(entity.getGuide() != null ? entity.getGuide().getId() : null) // ⚠️ ID uniquement
            .build();
    }

    // version “light” -> pour éviter stackoverflow
    public static GuideActiviteDto toLightDto(GuideActivite entity) {
        if (entity == null) return null;
        return GuideActiviteDto.builder()
            .id(entity.getId())
            .jour(entity.getJour())
            .ordre(entity.getOrdre())
            .activiteId(entity.getActivite() != null ? entity.getActivite().getId() : null)
            .build();
    }
}
