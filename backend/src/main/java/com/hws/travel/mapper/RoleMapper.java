package com.hws.travel.mapper;

import com.hws.travel.entity.Role;
import com.hws.travel.dto.RoleDto;

public class RoleMapper {
    private RoleMapper() {}

    public static RoleDto toDto(Role role) {
        if (role == null) return null;
        return RoleDto.builder()
            .id(role.getId())
            .name(role.getName())
            .build();
    }

    public static Role toEntity(RoleDto dto) {
        if (dto == null) return null;
        Role role = new Role();
        role.setId(dto.getId());
        role.setName(dto.getName());
        return role;
    }
}
