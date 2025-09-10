package com.hws.travel.service;

import com.hws.travel.dto.UserCreateDto;
import com.hws.travel.dto.UserDto;
import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDto> getAllUsers();
    Optional<UserDto> getUserById(Long id);
    UserDto saveUser(UserCreateDto userCreateDto);
    boolean deleteUser(Long id);
    Long findIdByEmail(String email);
}
