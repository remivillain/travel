package com.hws.travel.service.impl;

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
        validateGuideCreateDto(guideCreateDto);

        Guide guide = mapGuideCreateDtoToGuide(guideCreateDto);

        setGuideActivites(guide, guideCreateDto);
        setInvitedUsers(guide, guideCreateDto);

        Guide savedGuide = guideRepository.save(guide);
        return GuideMapper.toDto(savedGuide);
    }

    private void validateGuideCreateDto(GuideCreateDto guideCreateDto) {
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

    private void setGuideActivites(Guide guide, GuideCreateDto guideCreateDto) {
        if (guideCreateDto.getGuideActivites() == null || guideCreateDto.getGuideActivites().isEmpty()) {
            return;
        }
            // Vérification unicité (jour, ordre)
            Set<String> jourOrdreSet = new HashSet<>();
            for (var dto : guideCreateDto.getGuideActivites()) {
                String key = dto.getJour() + ":" + dto.getOrdre();
                if (!jourOrdreSet.add(key)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il ne peut pas y avoir deux activités avec le même ordre pour le même jour (jour=" + dto.getJour() + ", ordre=" + dto.getOrdre() + ").");
                }
            }
            try {
                Set<GuideActivite> guideActivites = guideCreateDto.getGuideActivites().stream()
                    .map(dto -> {
                        GuideActivite entity = new GuideActivite();
                        entity.setGuide(guide);
                        entity.setJour(dto.getJour());
                        entity.setOrdre(dto.getOrdre());
                        if (dto.getActiviteId() != null) {
                            Activite activite = activiteRepository.findById(dto.getActiviteId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activité non trouvée: " + dto.getActiviteId()));
                            entity.setActivite(activite);
                        }
                        return entity;
                    })
                    .collect(Collectors.toSet());
                guide.setGuideActivites(guideActivites);
            } catch (StackOverflowError e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur serveur : StackOverflowError lors de l'association des activités au guide. Vérifiez les relations cycliques ou le mapping DTO.");
            }
    }

    private void setInvitedUsers(Guide guide, GuideCreateDto guideCreateDto) {
        if (guideCreateDto.getInvitedUserIds() == null || guideCreateDto.getInvitedUserIds().isEmpty()) {
            return;
        }
        Set<User> invitedUsers = guideCreateDto.getInvitedUserIds().stream()
            .map(id -> userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Utilisateur invité non trouvé: " + id)))
            .collect(Collectors.toSet());
        guide.setInvitedUsers(invitedUsers);
    }


  

   
}
