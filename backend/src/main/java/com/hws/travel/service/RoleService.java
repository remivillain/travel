package com.hws.travel.service;

import com.hws.travel.entity.Role;
import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> getAllRoles();
    Optional<Role> getRoleById(Long id);
    Optional<Role> getRoleByName(String name);
    Role saveRole(Role role);
    void deleteRole(Long id);
}
