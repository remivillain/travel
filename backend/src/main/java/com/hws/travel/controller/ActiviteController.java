package com.hws.travel.controller;

import com.hws.travel.dto.ActiviteDto;
import com.hws.travel.entity.Activite;
import com.hws.travel.mapper.ActiviteMapper;
import com.hws.travel.service.impl.ActiviteServiceImpl;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/activites")
public class ActiviteController {
    private final ActiviteServiceImpl activiteService;

    public ActiviteController(ActiviteServiceImpl activiteService) {
        this.activiteService = activiteService;
    }

    @GetMapping
    public List<ActiviteDto> getAllActivites() {
        return activiteService.getAllActivites().stream()
            .map(ActiviteMapper::toDto)
            .toList();
    }

    @GetMapping("/{id}")
    public ActiviteDto getActiviteById(@PathVariable Long id) {
        return activiteService.getActiviteById(id)
            .map(ActiviteMapper::toDto)
            .orElse(null);
    }

    @PostMapping
    public ActiviteDto createActivite(@RequestBody ActiviteDto activiteDto) {
        Activite activite = ActiviteMapper.toEntity(activiteDto);
        return ActiviteMapper.toDto(activiteService.saveActivite(activite));
    }

    @DeleteMapping("/{id}")
    public void deleteActivite(@PathVariable Long id) {
        activiteService.deleteActivite(id);
    }
}
