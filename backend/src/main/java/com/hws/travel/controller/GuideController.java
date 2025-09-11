package com.hws.travel.controller;

import com.hws.travel.dto.GuideCreateDto;
import com.hws.travel.dto.GuideDto;
import com.hws.travel.dto.GuideUpdateDto;
import com.hws.travel.dto.GuideActiviteCreateDto;
import com.hws.travel.dto.UserInvitationDto;
import com.hws.travel.service.UserService;
import com.hws.travel.service.impl.GuideServiceImpl;
import com.hws.travel.security.AuthenticationHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/guides")
@Validated
@Tag(name = "Guides", description = "Gestion des guides de voyage avec système d'invitations")
@SecurityRequirement(name = "Bearer Authentication")
public class GuideController {
    private final GuideServiceImpl guideService;
    private final UserService userService;
    private final AuthenticationHelper authHelper;

    public GuideController(GuideServiceImpl guideService, UserService userService, AuthenticationHelper authHelper) {
        this.guideService = guideService;
        this.userService = userService;
        this.authHelper = authHelper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Récupérer tous les guides", 
        description = "Récupère la liste complète des guides (accessible aux administrateurs uniquement)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des guides récupérée avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - rôle ADMIN requis")
    })
    public List<GuideDto> getAllGuides() {
        return guideService.getAllGuides().stream()
            .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
        summary = "Récupérer un guide par ID", 
        description = "Récupère un guide spécifique selon son ID (accessible aux utilisateurs autorisés)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Guide récupéré avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - guide non accessible à cet utilisateur"),
        @ApiResponse(responseCode = "404", description = "Guide non trouvé")
    })
    public GuideDto getGuideById(
            @Parameter(description = "ID du guide", required = true)
            @PathVariable @Positive(message = "L'ID doit être positif") Long id, 
            Authentication authentication) {
        String email = authentication.getName();
        
        // Si l'utilisateur est admin, il peut accéder à tous les guides
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        if (isAdmin) {
            return guideService.getGuideById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "Guide non trouvé"));
        }
        
        // Pour les utilisateurs normaux, vérifier les permissions
        // Essayer d'abord de récupérer l'ID depuis le JWT
        Long userId = authHelper.getCurrentUserId();
        if (userId == null) {
            // Fallback : récupérer l'ID depuis l'email
            userId = userService.findIdByEmail(email);
        }
        return guideService.getGuideByIdForUser(id, userId);
    }

    @GetMapping("/mes-guides")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<GuideDto> getGuidesForCurrentUser(Authentication authentication) {
        // Essayer d'abord de récupérer l'ID depuis le JWT
        Long userId = authHelper.getCurrentUserId();
        if (userId == null) {
            // Fallback : récupérer l'ID depuis l'email
            String email = authentication.getName();
            userId = userService.findIdByEmail(email);
        }
        return guideService.getGuidesForUser(userId).stream().toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public GuideDto createGuide(@Valid @RequestBody GuideCreateDto guideCreateDto) {
        return guideService.saveGuide(guideCreateDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public GuideDto updateGuide(@PathVariable @Positive(message = "L'ID doit être positif") Long id, @Valid @RequestBody GuideUpdateDto guideUpdateDto) {
        return guideService.updateGuide(id, guideUpdateDto);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteGuide(@PathVariable @Positive(message = "L'ID doit être positif") Long id) {
        guideService.deleteGuide(id);
    }

    @PostMapping("/{id}/activities")
    @PreAuthorize("hasRole('ADMIN')")
    public GuideDto addActivityToGuide(@PathVariable @Positive(message = "L'ID doit être positif") Long id, 
                                      @Valid @RequestBody GuideActiviteCreateDto guideActiviteCreateDto) {
        return guideService.addActivityToGuide(id, guideActiviteCreateDto);
    }

    @PostMapping("/{id}/activities/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public GuideDto addActivitiesToGuide(@PathVariable @Positive(message = "L'ID doit être positif") Long id,
                                        @Valid @RequestBody List<GuideActiviteCreateDto> guideActivites) {
        return guideService.addActivitiesToGuide(id, guideActivites);
    }

    @DeleteMapping("/{id}/activities/{activityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public GuideDto removeActivityFromGuide(@PathVariable @Positive(message = "L'ID doit être positif") Long id,
                                           @PathVariable @Positive(message = "L'ID de l'activité doit être positif") Long activityId) {
        return guideService.removeActivityFromGuide(id, activityId);
    }

    @PostMapping("/{id}/invite")
    @PreAuthorize("hasRole('ADMIN')")
    public GuideDto inviteUserToGuide(
            @PathVariable @Positive(message = "L'ID doit être positif") Long id,
            @Valid @RequestBody UserInvitationDto userInvitationDto) {
        return guideService.inviteUserToGuide(id, userInvitationDto.getUserId());
    }

    @DeleteMapping("/{id}/invite")
    @PreAuthorize("hasRole('ADMIN')")
    public GuideDto removeUserFromGuide(
            @PathVariable @Positive(message = "L'ID doit être positif") Long id,
            @Valid @RequestBody UserInvitationDto userInvitationDto) {
        return guideService.removeUserFromGuide(id, userInvitationDto.getUserId());
    }
}
