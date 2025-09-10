package com.hws.travel.controller;

import com.hws.travel.dto.ActiviteDto;
import com.hws.travel.service.impl.ActiviteServiceImpl;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/activites")
public class ActiviteController {
    private final ActiviteServiceImpl activiteService;

    public ActiviteController(ActiviteServiceImpl activiteService) {
        this.activiteService = activiteService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ActiviteDto> getAllActivites() {
        return activiteService.getAllActivites().stream()
            .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ActiviteDto getActiviteById(@PathVariable Long id) {
        return activiteService.getActiviteById(id)
            .orElse(null);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ActiviteDto createActivite(@RequestBody ActiviteDto activiteDto) {
        return activiteService.saveActivite(activiteDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteActivite(@PathVariable Long id) {
        activiteService.deleteActivite(id);
    }
}
