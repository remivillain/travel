package com.hws.travel.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto {
    private Long id;
    
    @NotBlank(message = "Le nom du rôle est obligatoire.")
    @Size(max = 50, message = "Le nom du rôle ne peut pas dépasser 50 caractères.")
    @Pattern(regexp = "^[A-Z_]+$", message = "Le nom du rôle doit être en majuscules et ne contenir que des lettres et des underscores.")
    private String name;
}
