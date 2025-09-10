package com.hws.travel.controller;

import com.hws.travel.dto.GuideCreateDto;
import com.hws.travel.dto.GuideDto;
import com.hws.travel.dto.GuideUpdateDto;
import com.hws.travel.service.impl.GuideServiceImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/guides")
public class GuideController {
    
    private final GuideServiceImpl guideService;

    public GuideController(GuideServiceImpl guideService) {
        this.guideService = guideService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<GuideDto> getAllGuides() {
        return guideService.getAllGuides().stream()
            .toList();
    }

    @GetMapping("/{id}")
    public GuideDto getGuideById(@PathVariable Long id) {
        return guideService.getGuideById(id)
            .orElse(null);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public GuideDto createGuide(@RequestBody GuideCreateDto guideCreateDto) {
        return guideService.saveGuide(guideCreateDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public GuideDto updateGuide(@PathVariable Long id, @RequestBody GuideUpdateDto guideUpdateDto) {
        return guideService.updateGuide(id, guideUpdateDto);
    }
    
    @DeleteMapping("/{id}")
    public void deleteGuide(@PathVariable Long id) {
        guideService.deleteGuide(id);
    }
}
