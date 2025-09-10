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
import com.hws.travel.mapper.GuideMapper;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    private Set<Mobilite> mapMobilites(Set<String> mobilites) {
        if (mobilites == null) return Collections.emptySet();
        try {
            return mobilites.stream()
                .map(Mobilite::valueOf)
                .collect(Collectors.toSet());
        } catch (IllegalArgumentException e) {
            String invalid = e.getMessage().replace("No enum constant com.hws.travel.entity.enums.Mobilite.", "");
            String validValues = String.join(", ", Arrays.stream(Mobilite.values()).map(Enum::name).toList());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobilité invalide : '" + invalid + "'. Valeurs acceptées : [" + validValues + "]");
        }
    }
    
    private Set<Saison> mapSaisons(Set<String> saisons) {
        if (saisons == null) return Collections.emptySet();
        try {
            return saisons.stream()
                .map(Saison::valueOf)
                .collect(Collectors.toSet());
        } catch (IllegalArgumentException e) {
            String invalid = e.getMessage().replace("No enum constant com.hws.travel.entity.enums.Saison.", "");
            String validValues = String.join(", ", Arrays.stream(Saison.values()).map(Enum::name).toList());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saison invalide : '" + invalid + "'. Valeurs acceptées : [" + validValues + "]");
        }
    }
    
    private Set<PourQui> mapPourQui(Set<String> pourQui) {
        if (pourQui == null) return Collections.emptySet();
        try {
            return pourQui.stream()
                .map(PourQui::valueOf)
                .collect(Collectors.toSet());
        } catch (IllegalArgumentException e) {
            String invalid = e.getMessage().replace("No enum constant com.hws.travel.entity.enums.PourQui.", "");
            String validValues = String.join(", ", Arrays.stream(PourQui.values()).map(Enum::name).toList());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PourQui invalide : '" + invalid + "'. Valeurs acceptées : [" + validValues + "]");
        }
    }

    private void setGuideActivites(Guide guide, Object dto) {
        log.debug("setGuideActivites called for guideId={} with dto={}", guide.getId(), dto);
        Set<?> guideActivitesRaw = null;
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
        Set<String> jourOrdreSet = new HashSet<>();
        Set<GuideActivite> guideActivites = new HashSet<>();
        for (Object o : guideActivitesRaw) {
            var activiteDto = (com.hws.travel.dto.GuideActiviteCreateDto) o;
            String key = activiteDto.getJour() + ":" + activiteDto.getOrdre();
            if (!jourOrdreSet.add(key)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Il ne peut pas y avoir deux activités avec le même ordre pour le même jour (jour=" 
                    + activiteDto.getJour() + ", ordre=" + activiteDto.getOrdre() + ").");
            }

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

        if (guide.getGuideActivites() == null) {
            guide.setGuideActivites(new HashSet<>());
        } else {
            guide.getGuideActivites().clear();
        }
        guide.getGuideActivites().addAll(guideActivites);
        log.debug("GuideActivites set: {}", guide.getGuideActivites());
    }

    private void setInvitedUsers(Guide guide, Object dto) {
        log.debug("setInvitedUsers called for guideId={} with dto={}", guide.getId(), dto);
        Set<Long> userIds = null;
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

        Set<User> invitedUsers = userIds.stream()
            .map(id -> userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Utilisateur invité non trouvé: " + id)))
            .collect(Collectors.toSet());

        if (guide.getInvitedUsers() == null) {
            guide.setInvitedUsers(new HashSet<>());
        } else {
            guide.getInvitedUsers().clear();
        }
        guide.getInvitedUsers().addAll(invitedUsers);

        log.debug("InvitedUsers set: {}", invitedUsers);
    }


  

   
}
