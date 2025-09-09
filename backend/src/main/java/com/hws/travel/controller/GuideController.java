package com.hws.travel.controller;

import com.hws.travel.dto.GuideDto;
import com.hws.travel.entity.Guide;
import com.hws.travel.mapper.GuideMapper;
import com.hws.travel.service.impl.GuideServiceImpl;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/guides")
public class GuideController {
    private final GuideServiceImpl guideService;

    public GuideController(GuideServiceImpl guideService) {
        this.guideService = guideService;
    }

    @GetMapping
    public List<GuideDto> getAllGuides() {
        return guideService.getAllGuides().stream()
            .map(GuideMapper::toDto)
            .toList();
    }

    @GetMapping("/{id}")
    public GuideDto getGuideById(@PathVariable Long id) {
        return guideService.getGuideById(id)
            .map(GuideMapper::toDto)
            .orElse(null);
    }

    @PostMapping
    public GuideDto createGuide(@RequestBody GuideDto guideDto) {
        Guide guide = GuideMapper.toEntity(guideDto);
        return GuideMapper.toDto(guideService.saveGuide(guide));
    }

    @DeleteMapping("/{id}")
    public void deleteGuide(@PathVariable Long id) {
        guideService.deleteGuide(id);
    }
}
