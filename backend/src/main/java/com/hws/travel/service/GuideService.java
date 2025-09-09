package com.hws.travel.service;

import com.hws.travel.entity.Guide;
import java.util.List;
import java.util.Optional;

public interface GuideService {
    List<Guide> getAllGuides();
    Optional<Guide> getGuideById(Long id);
    Guide saveGuide(Guide guide);
    void deleteGuide(Long id);
}
