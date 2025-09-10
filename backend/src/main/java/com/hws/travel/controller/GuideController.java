package com.hws.travel.controller;

import com.hws.travel.dto.GuideCreateDto;
import com.hws.travel.dto.GuideDto;
import com.hws.travel.dto.GuideUpdateDto;
import com.hws.travel.service.UserService;
import com.hws.travel.service.impl.GuideServiceImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/guides")
public class GuideController {
    private final GuideServiceImpl guideService;
    private final UserService userService;

    public GuideController(GuideServiceImpl guideService, UserService userService) {
        this.guideService = guideService;
        this.userService = userService;
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

    @GetMapping("/mes-guides")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<GuideDto> getGuidesForCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        Long userId = userService.findIdByEmail(email);
        return guideService.getGuidesForUser(userId).stream().toList();
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
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteGuide(@PathVariable Long id) {
        guideService.deleteGuide(id);
    }
}
