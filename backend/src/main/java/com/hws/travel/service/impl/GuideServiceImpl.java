package com.hws.travel.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hws.travel.repository.ActiviteRepository;
import com.hws.travel.repository.GuideRepository;
import com.hws.travel.repository.UserRepository;
import com.hws.travel.service.GuideService;
import com.hws.travel.entity.Activite;
import com.hws.travel.entity.Guide;
import com.hws.travel.entity.GuideActivite;
import com.hws.travel.entity.User;
import com.hws.travel.entity.enums.Mobilite;
import com.hws.travel.entity.enums.PourQui;
import com.hws.travel.entity.enums.Saison;
import com.hws.travel.dto.GuideDto;
import com.hws.travel.dto.GuideUpdateDto;
import com.hws.travel.dto.GuideActiviteCreateDto;
import com.hws.travel.mapper.GuideMapper;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.hws.travel.dto.GuideCreateDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class GuideServiceImpl implements GuideService {
    private static final Logger log = LoggerFactory.getLogger(GuideServiceImpl.class);
    
    private final GuideRepository guideRepository;
    private final UserRepository userRepository;
    private final ActiviteRepository activiteRepository;

    public GuideServiceImpl(GuideRepository guideRepository, UserRepository userRepository, ActiviteRepository activiteRepository) {
        this.guideRepository = guideRepository;
        this.userRepository = userRepository;
        this.activiteRepository = activiteRepository;
    }

    @Override
    public List<GuideDto> getAllGuides() {
        return guideRepository.findAll().stream()
            .map(GuideMapper::toDto)
            .toList();
    }

    @Override
    public Optional<GuideDto> getGuideById(Long id) {
        return guideRepository.findById(id).map(GuideMapper::toDto);
    }

    @Override
    public GuideDto getGuideByIdForUser(Long id, Long userId) {
        Guide guide = guideRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide non trouvé"));
        
        // Vérifier que l'utilisateur a accès à ce guide
        boolean hasAccess = guide.getInvitedUsers().stream()
            .anyMatch(user -> user.getId().equals(userId));
        
        if (!hasAccess) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'avez pas accès à ce guide");
        }
        
        return GuideMapper.toDto(guide);
    }


    @Override
    public void deleteGuide(Long id) {
        guideRepository.deleteById(id);
    }

    @Override
    public GuideDto saveGuide(GuideCreateDto guideCreateDto) {
        validateGuideDto(guideCreateDto);

        Guide guide = mapGuideCreateDtoToGuide(guideCreateDto);

        setGuideActivites(guide, guideCreateDto);
        setInvitedUsers(guide, guideCreateDto);

        Guide savedGuide = guideRepository.save(guide);
        return GuideMapper.toDto(savedGuide);
    }

    public GuideDto updateGuide(Long id, GuideUpdateDto guideUpdateDto) {
        log.info("updateGuide called with id={} and payload={}", id, guideUpdateDto);
        try {
            Guide guide = guideRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide non trouvé"));

            validateGuideDto(guideUpdateDto);

            // Mise à jour des champs principaux si non null
            if (guideUpdateDto.getTitre() != null) guide.setTitre(guideUpdateDto.getTitre());
            if (guideUpdateDto.getDescription() != null) guide.setDescription(guideUpdateDto.getDescription());
            if (guideUpdateDto.getNombreJours() != null) guide.setNombreJours(guideUpdateDto.getNombreJours());
            if (guideUpdateDto.getMobilites() != null) guide.setMobilites(mapMobilites(guideUpdateDto.getMobilites()));
            if (guideUpdateDto.getSaisons() != null) guide.setSaisons(mapSaisons(guideUpdateDto.getSaisons()));
            if (guideUpdateDto.getPourQui() != null) guide.setPourQui(mapPourQui(guideUpdateDto.getPourQui()));

            // Mise à jour des activités (remplacement complet si fourni)
            if (guideUpdateDto.getGuideActivites() != null) {
                log.info("setGuideActivites called with guideActivites={}", guideUpdateDto.getGuideActivites());
                setGuideActivites(guide, guideUpdateDto);
            }
            // Mise à jour des invités (remplacement complet si fourni)
            if (guideUpdateDto.getInvitedUserIds() != null) {
                log.info("setInvitedUsers called with invitedUserIds={}", guideUpdateDto.getInvitedUserIds());
                setInvitedUsers(guide, guideUpdateDto);
            }

            Guide savedGuide = guideRepository.save(guide);
            log.info("Guide updated successfully: {}", savedGuide);
            return GuideMapper.toDto(savedGuide);
        } catch (ResponseStatusException e) {
            log.error("ResponseStatusException in updateGuide: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Exception in updateGuide: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur : " + e.getMessage());
        }
    }

    public List<GuideDto> getGuidesForUser(Long userId) {
        return guideRepository.findGuidesByInvitedUserId(userId).stream()
            .map(com.hws.travel.mapper.GuideMapper::toDto)
            .toList();
    }

    @Override
    public GuideDto addActivityToGuide(Long guideId, GuideActiviteCreateDto guideActiviteCreateDto) {
        // Vérifier que le guide existe
        Guide guide = guideRepository.findById(guideId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide non trouvé"));

        // Vérifier l'unicité (jour, ordre) pour éviter les conflits
        boolean conflictExists = guide.getGuideActivites().stream()
            .anyMatch(ga -> ga.getJour() == guideActiviteCreateDto.getJour() 
                         && ga.getOrdre() == guideActiviteCreateDto.getOrdre());
        
        if (conflictExists) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Il existe déjà une activité avec cet ordre pour le jour " + guideActiviteCreateDto.getJour());
        }

        // Créer et ajouter la nouvelle activité
        GuideActivite guideActivite = new GuideActivite();
        guideActivite.setGuide(guide);
        guideActivite.setJour(guideActiviteCreateDto.getJour());
        guideActivite.setOrdre(guideActiviteCreateDto.getOrdre());
        
        if (guideActiviteCreateDto.getActiviteId() != null) {
            Activite activite = activiteRepository.findById(guideActiviteCreateDto.getActiviteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Activité non trouvée: " + guideActiviteCreateDto.getActiviteId()));
            guideActivite.setActivite(activite);
        }

        guide.getGuideActivites().add(guideActivite);
        Guide savedGuide = guideRepository.save(guide);
        
        return GuideMapper.toDto(savedGuide);
    }

    @Override
    public GuideDto addActivitiesToGuide(Long guideId, List<GuideActiviteCreateDto> guideActivites) {
        // Vérifier que le guide existe
        Guide guide = guideRepository.findById(guideId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide non trouvé"));

        // Vérifier l'unicité des nouvelles activités entre elles et avec les existantes
        List<String> existingKeys = guide.getGuideActivites().stream()
            .map(ga -> ga.getJour() + ":" + ga.getOrdre())
            .collect(Collectors.toList());
        
        List<String> newKeys = new ArrayList<>();
        for (GuideActiviteCreateDto dto : guideActivites) {
            String key = dto.getJour() + ":" + dto.getOrdre();
            if (existingKeys.contains(key) || newKeys.contains(key)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Conflit détecté : jour=" + dto.getJour() + ", ordre=" + dto.getOrdre());
            }
            newKeys.add(key);
        }

        // Créer et ajouter toutes les nouvelles activités
        for (GuideActiviteCreateDto dto : guideActivites) {
            GuideActivite guideActivite = new GuideActivite();
            guideActivite.setGuide(guide);
            guideActivite.setJour(dto.getJour());
            guideActivite.setOrdre(dto.getOrdre());
            
            if (dto.getActiviteId() != null) {
                Activite activite = activiteRepository.findById(dto.getActiviteId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Activité non trouvée: " + dto.getActiviteId()));
                guideActivite.setActivite(activite);
            }

            guide.getGuideActivites().add(guideActivite);
        }

        Guide savedGuide = guideRepository.save(guide);
        return GuideMapper.toDto(savedGuide);
    }

    private void validateGuideDto(Object dto) {
        if (dto instanceof GuideCreateDto guideCreateDto) {
            validateGuideCreateDtoFields(guideCreateDto);
        } else if (dto instanceof GuideUpdateDto guideUpdateDto) {
            validateGuideUpdateDtoFields(guideUpdateDto);
        }
    }

    private void validateGuideCreateDtoFields(GuideCreateDto guideCreateDto) {
        if (guideCreateDto.getTitre() == null || guideCreateDto.getTitre().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le titre est obligatoire.");
        if (guideCreateDto.getDescription() == null || guideCreateDto.getDescription().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La description est obligatoire.");
        if (guideCreateDto.getNombreJours() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nombre de jours doit être supérieur à 0.");
        if (guideCreateDto.getMobilites() == null || guideCreateDto.getMobilites().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Au moins une mobilité est requise.");
        if (guideCreateDto.getSaisons() == null || guideCreateDto.getSaisons().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Au moins une saison est requise.");
        if (guideCreateDto.getPourQui() == null || guideCreateDto.getPourQui().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Au moins une option 'pour qui' est requise.");
    }

    private void validateGuideUpdateDtoFields(GuideUpdateDto guideUpdateDto) {
        if (guideUpdateDto.getTitre() != null && guideUpdateDto.getTitre().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le titre ne peut pas être vide.");
        if (guideUpdateDto.getDescription() != null && guideUpdateDto.getDescription().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La description ne peut pas être vide.");
        if (guideUpdateDto.getNombreJours() != null && guideUpdateDto.getNombreJours() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nombre de jours doit être supérieur à 0.");
        if (guideUpdateDto.getMobilites() != null && guideUpdateDto.getMobilites().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Au moins une mobilité est requise si renseignée.");
        if (guideUpdateDto.getSaisons() != null && guideUpdateDto.getSaisons().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Au moins une saison est requise si renseignée.");
        if (guideUpdateDto.getPourQui() != null && guideUpdateDto.getPourQui().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Au moins une option 'pour qui' est requise si renseignée.");
    }

    private Guide mapGuideCreateDtoToGuide(GuideCreateDto guideCreateDto) {
        Guide guide = new Guide();
        guide.setTitre(guideCreateDto.getTitre());
        guide.setDescription(guideCreateDto.getDescription());
        guide.setNombreJours(guideCreateDto.getNombreJours());
        guide.setMobilites(mapMobilites(guideCreateDto.getMobilites()));
        guide.setSaisons(mapSaisons(guideCreateDto.getSaisons()));
        guide.setPourQui(mapPourQui(guideCreateDto.getPourQui()));
        return guide;
    }

    private List<Mobilite> mapMobilites(List<String> mobilites) {
        if (mobilites == null) return Collections.emptyList();
        try {
            return mobilites.stream()
                .map(Mobilite::valueOf)
                .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            String invalid = e.getMessage().replace("No enum constant com.hws.travel.entity.enums.Mobilite.", "");
            String validValues = String.join(", ", Arrays.stream(Mobilite.values()).map(Enum::name).toList());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobilité invalide : '" + invalid + "'. Valeurs acceptées : [" + validValues + "]");
        }
    }
    
    private List<Saison> mapSaisons(List<String> saisons) {
        if (saisons == null) return Collections.emptyList();
        try {
            return saisons.stream()
                .map(Saison::valueOf)
                .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            String invalid = e.getMessage().replace("No enum constant com.hws.travel.entity.enums.Saison.", "");
            String validValues = String.join(", ", Arrays.stream(Saison.values()).map(Enum::name).toList());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saison invalide : '" + invalid + "'. Valeurs acceptées : [" + validValues + "]");
        }
    }
    
    private List<PourQui> mapPourQui(List<String> pourQui) {
        if (pourQui == null) return Collections.emptyList();
        try {
            return pourQui.stream()
                .map(PourQui::valueOf)
                .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            String invalid = e.getMessage().replace("No enum constant com.hws.travel.entity.enums.PourQui.", "");
            String validValues = String.join(", ", Arrays.stream(PourQui.values()).map(Enum::name).toList());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PourQui invalide : '" + invalid + "'. Valeurs acceptées : [" + validValues + "]");
        }
    }

    private void setGuideActivites(Guide guide, Object dto) {
        log.debug("setGuideActivites called for guideId={} with dto={}", guide.getId(), dto);
        List<?> guideActivitesRaw = null;
        if (dto instanceof GuideCreateDto guideCreateDto) {
            guideActivitesRaw = guideCreateDto.getGuideActivites();
        } else if (dto instanceof GuideUpdateDto guideUpdateDto) {
            guideActivitesRaw = guideUpdateDto.getGuideActivites();
        }
        if (guideActivitesRaw == null || guideActivitesRaw.isEmpty()) {
            if (guide.getGuideActivites() != null) {
                guide.getGuideActivites().clear();
            }
            return;
        }

        // Vérification unicité (jour, ordre)
        List<String> jourOrdreList = new ArrayList<>();
        List<GuideActivite> guideActivites = new ArrayList<>();
        for (Object o : guideActivitesRaw) {
            var activiteDto = (com.hws.travel.dto.GuideActiviteCreateDto) o;
            String key = activiteDto.getJour() + ":" + activiteDto.getOrdre();
            if (jourOrdreList.contains(key)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Il ne peut pas y avoir deux activités avec le même ordre pour le même jour (jour=" 
                    + activiteDto.getJour() + ", ordre=" + activiteDto.getOrdre() + ").");
            }
            jourOrdreList.add(key);

            GuideActivite entity = new GuideActivite();
            entity.setGuide(guide);
            entity.setJour(activiteDto.getJour());
            entity.setOrdre(activiteDto.getOrdre());
            if (activiteDto.getActiviteId() != null) {
                Activite activite = activiteRepository.findById(activiteDto.getActiviteId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Activité non trouvée: " + activiteDto.getActiviteId()));
                entity.setActivite(activite);
            }
            guideActivites.add(entity);
        }
        log.info("GuideActivites -------- {}", guideActivites);
        log.info("getGuideActivites -------- {}", guide.getGuideActivites());

        if (guide.getGuideActivites() == null) {
            guide.setGuideActivites(new ArrayList<>());
        } else {
            guide.getGuideActivites().clear();
        }
        guide.getGuideActivites().addAll(guideActivites);
    }

    private void setInvitedUsers(Guide guide, Object dto) {
        log.debug("setInvitedUsers called for guideId={} with dto={}", guide.getId(), dto);
        List<Long> userIds = null;
        if (dto instanceof GuideCreateDto guideCreateDto) {
            userIds = guideCreateDto.getInvitedUserIds();
        } else if (dto instanceof GuideUpdateDto guideUpdateDto) {
            userIds = guideUpdateDto.getInvitedUserIds();
        }

        if (userIds == null || userIds.isEmpty()) {
            if (guide.getInvitedUsers() != null) {
                guide.getInvitedUsers().clear(); // vider proprement
            }
            return;
        }

        List<User> invitedUsers = userIds.stream()
            .map(id -> userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Utilisateur invité non trouvé: " + id)))
            .collect(Collectors.toList());

        if (guide.getInvitedUsers() == null) {
            guide.setInvitedUsers(new ArrayList<>());
        } else {
            guide.getInvitedUsers().clear();
        }
        guide.getInvitedUsers().addAll(invitedUsers);

        log.debug("InvitedUsers set: {}", invitedUsers);
    }

    @Override
    public GuideDto removeActivityFromGuide(Long guideId, Long activityId) {
        // Vérifier que le guide existe
        Guide guide = guideRepository.findById(guideId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guide non trouvé"));

        // Trouver et supprimer l'activité par ID
        boolean removed = guide.getGuideActivites().removeIf(ga -> 
            ga.getId().equals(activityId));
        
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                "Aucune activité trouvée avec l'ID " + activityId);
        }

        Guide savedGuide = guideRepository.save(guide);
        return GuideMapper.toDto(savedGuide);
    }
}
