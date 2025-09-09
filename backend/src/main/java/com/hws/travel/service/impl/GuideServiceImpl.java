package com.hws.travel.service.impl;

import com.hws.travel.entity.Guide;
import com.hws.travel.repository.GuideRepository;
import com.hws.travel.service.GuideService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GuideServiceImpl implements GuideService {
    private final GuideRepository guideRepository;

    public GuideServiceImpl(GuideRepository guideRepository) {
        this.guideRepository = guideRepository;
    }

    @Override
    public List<Guide> getAllGuides() {
        return guideRepository.findAll();
    }

    @Override
    public Optional<Guide> getGuideById(Long id) {
        return guideRepository.findById(id);
    }

    @Override
    public Guide saveGuide(Guide guide) {
        return guideRepository.save(guide);
    }

    @Override
    public void deleteGuide(Long id) {
        guideRepository.deleteById(id);
    }
}
