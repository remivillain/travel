package com.hws.travel.controller;

import com.hws.travel.dto.RoleDto;
import com.hws.travel.entity.Role;
import com.hws.travel.mapper.RoleMapper;
import com.hws.travel.service.impl.RoleServiceImpl;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleServiceImpl roleService;

    public RoleController(RoleServiceImpl roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public List<RoleDto> getAllRoles() {
        return roleService.getAllRoles().stream()
            .map(RoleMapper::toDto)
            .toList();
    }

    @GetMapping("/{id}")
    public RoleDto getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id)
            .map(RoleMapper::toDto)
            .orElse(null);
    }

    @PostMapping
    public RoleDto createRole(@RequestBody RoleDto roleDto) {
        Role role = RoleMapper.toEntity(roleDto);
        return RoleMapper.toDto(roleService.saveRole(role));
    }

    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
    }
}
