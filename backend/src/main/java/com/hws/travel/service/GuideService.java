package com.hws.travel.service;

import com.hws.travel.dto.GuideDto;
import com.hws.travel.dto.GuideCreateDto;
import java.util.List;
import java.util.Optional;

import com.hws.travel.dto.GuideActiviteCreateDto;

public interface GuideService {
    List<GuideDto> getAllGuides();
    Optional<GuideDto> getGuideById(Long id);
    GuideDto getGuideByIdForUser(Long id, Long userId);
    GuideDto saveGuide(GuideCreateDto guideCreateDto);
    void deleteGuide(Long id);
    GuideDto addActivityToGuide(Long guideId, GuideActiviteCreateDto guideActiviteCreateDto);
    GuideDto addActivitiesToGuide(Long guideId, List<GuideActiviteCreateDto> guideActivites);
    GuideDto removeActivityFromGuide(Long guideId, Long activityId);
}
