package com.hws.travel.service;

import com.hws.travel.entity.Guide;

import com.hws.travel.dto.ActiviteDto;
import java.util.List;
import java.util.Optional;

public interface ActiviteService {
    List<ActiviteDto> getAllActivites();
    Optional<ActiviteDto> getActiviteById(Long id);
    ActiviteDto saveActivite(ActiviteDto activiteDto);
    ActiviteDto saveActiviteForGuide(ActiviteDto activiteDto, Guide guide);
    void deleteActivite(Long id);
    void validateActivite(ActiviteDto activiteDto, int nombreJours);
}
