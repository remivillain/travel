package com.hws.travel.mapper;

import com.hws.travel.entity.Role;
import com.hws.travel.entity.User;
import com.hws.travel.dto.UserDto;
import java.util.stream.Collectors;

public class UserMapper {
    private UserMapper() {}

    public static UserDto toDto(User user) {
        if (user == null) return null;
        return UserDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .roles(user.getRoles() != null ? user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()) : null)
            .build();
    }

    public static User toEntity(UserDto dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        // Pour les rôles, il faudra gérer l'association dans le service
        return user;
    }
}
