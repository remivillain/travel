package com.hws.travel.controller;

import com.hws.travel.dto.UserDto;
import com.hws.travel.entity.User;
import com.hws.travel.mapper.UserMapper;
import com.hws.travel.service.impl.UserServiceImpl;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers().stream()
            .map(UserMapper::toDto)
            .toList();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(UserMapper::toDto)
            .orElse(null);
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        return UserMapper.toDto(userService.saveUser(user));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
