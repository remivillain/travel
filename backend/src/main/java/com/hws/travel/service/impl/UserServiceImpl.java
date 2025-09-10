
package com.hws.travel.service.impl;

import com.hws.travel.entity.Role;
import com.hws.travel.entity.User;
import com.hws.travel.dto.UserDto;
import com.hws.travel.mapper.UserMapper;
import com.hws.travel.dto.UserCreateDto;
import com.hws.travel.repository.RoleRepository;
import com.hws.travel.repository.UserRepository;
import com.hws.travel.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
            .map(UserMapper::toDto)
            .toList();
    }

    @Override
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id).map(UserMapper::toDto);
    }

    @Override
    public UserDto saveUser(UserCreateDto userCreateDto) {
        // Validation email
        if (userCreateDto.getEmail() == null || userCreateDto.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'email est obligatoire.");
        }
        if (userRepository.findByEmail(userCreateDto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cet email existe déjà.");
        }
        if (userCreateDto.getPassword() == null || userCreateDto.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le mot de passe est obligatoire.");
        }
        if (userCreateDto.getRoles() == null || userCreateDto.getRoles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Au moins un rôle est requis.");
        }
        // Vérification que tous les rôles existent
        boolean allRolesExist = userCreateDto.getRoles().stream()
            .allMatch(roleName -> roleRepository.findByName(roleName).isPresent());
        if (!allRolesExist) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un ou plusieurs rôles sont invalides.");
        }
        // Mapping User
        User user = new User();
        user.setEmail(userCreateDto.getEmail());
        user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
        List<Role> roles = userCreateDto.getRoles().stream()
            .map(roleName -> roleRepository.findByName(roleName).orElse(null))
            .filter(Objects::nonNull)
            .toList();
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        return UserMapper.toDto(savedUser);
    }

    @Override
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
