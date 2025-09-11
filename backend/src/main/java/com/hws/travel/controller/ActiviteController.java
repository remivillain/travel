package com.hws.travel.controller;

import com.hws.travel.dto.ActiviteDto;
import com.hws.travel.service.impl.ActiviteServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/activites")
@Validated
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
    public ActiviteDto getActiviteById(@PathVariable @Positive(message = "L'ID doit être positif") Long id) {
        return activiteService.getActiviteById(id)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Activité non trouvée"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ActiviteDto createActivite(@Valid @RequestBody ActiviteDto activiteDto) {
        return activiteService.saveActivite(activiteDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ActiviteDto updateActivite(@PathVariable @Positive(message = "L'ID doit être positif") Long id, @Valid @RequestBody ActiviteDto activiteDto) {
        activiteDto.setId(id);
        return activiteService.saveActivite(activiteDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteActivite(@PathVariable @Positive(message = "L'ID doit être positif") Long id) {
        activiteService.deleteActivite(id);
    }
}
