package com.hws.travel.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDto {
    @NotBlank(message = "L'email est obligatoire.")
    @Email(message = "Le format de l'email n'est pas valide.")
    @Size(max = 255, message = "L'email ne peut pas dépasser 255 caractères.")
    private String email;
    
    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 6, max = 255, message = "Le mot de passe doit contenir entre 6 et 255 caractères.")
    private String password;
    
    @NotEmpty(message = "Au moins un rôle est requis.")
    private Set<String> roles;
}
