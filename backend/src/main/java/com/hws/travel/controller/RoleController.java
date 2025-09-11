package com.hws.travel.controller;

import com.hws.travel.dto.RoleDto;
import com.hws.travel.entity.Role;
import com.hws.travel.mapper.RoleMapper;
import com.hws.travel.service.impl.RoleServiceImpl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/roles")
@Validated
public class RoleController {
    private final RoleServiceImpl roleService;

    public RoleController(RoleServiceImpl roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleDto> getAllRoles() {
        return roleService.getAllRoles().stream()
            .map(RoleMapper::toDto)
            .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RoleDto getRoleById(@PathVariable @Positive(message = "L'ID doit être positif") Long id) {
        return roleService.getRoleById(id)
            .map(RoleMapper::toDto)
            .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Rôle non trouvé"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RoleDto createRole(@Valid @RequestBody RoleDto roleDto) {
        Role role = RoleMapper.toEntity(roleDto);
        return RoleMapper.toDto(roleService.saveRole(role));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRole(@PathVariable @Positive(message = "L'ID doit être positif") Long id) {
        roleService.deleteRole(id);
    }
}
