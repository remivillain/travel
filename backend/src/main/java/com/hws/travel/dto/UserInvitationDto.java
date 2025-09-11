package com.hws.travel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UserInvitationDto {
    @NotNull(message = "L'ID utilisateur est obligatoire")
    @Positive(message = "L'ID utilisateur doit être positif")
    private Long userId;
}
