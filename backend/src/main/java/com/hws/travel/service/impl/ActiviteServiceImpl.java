
package com.hws.travel.service.impl;

import com.hws.travel.entity.Guide;
import com.hws.travel.entity.enums.ActiviteCategorie;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.hws.travel.entity.Activite;
import com.hws.travel.dto.ActiviteDto;
import com.hws.travel.mapper.ActiviteMapper;
import com.hws.travel.repository.ActiviteRepository;
import com.hws.travel.service.ActiviteService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ActiviteServiceImpl implements ActiviteService {
    private final ActiviteRepository activiteRepository;

    public ActiviteServiceImpl(ActiviteRepository activiteRepository) {
        this.activiteRepository = activiteRepository;
    }

    @Override
    public List<ActiviteDto> getAllActivites() {
        return activiteRepository.findAll().stream()
            .map(ActiviteMapper::toDto)
            .toList();
    }

    @Override
    public Optional<ActiviteDto> getActiviteById(Long id) {
        return activiteRepository.findById(id).map(ActiviteMapper::toDto);
    }

    @Override
    public ActiviteDto saveActivite(ActiviteDto activiteDto) {
        validateActiviteCategorie(activiteDto.getCategorie());
        Activite activite;
        if (activiteDto.getId() != null) {
            activite = activiteRepository.findById(activiteDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Activité non trouvée"));
            // Mise à jour des champs
            if (activiteDto.getTitre() != null) activite.setTitre(activiteDto.getTitre());
            if (activiteDto.getDescription() != null) activite.setDescription(activiteDto.getDescription());
            if (activiteDto.getCategorie() != null) activite.setCategorie(com.hws.travel.entity.enums.ActiviteCategorie.valueOf(activiteDto.getCategorie()));
            if (activiteDto.getAdresse() != null) activite.setAdresse(activiteDto.getAdresse());
            if (activiteDto.getTelephone() != null) activite.setTelephone(activiteDto.getTelephone());
            if (activiteDto.getHorairesOuverture() != null) activite.setHorairesOuverture(activiteDto.getHorairesOuverture());
            if (activiteDto.getSiteInternet() != null) activite.setSiteInternet(activiteDto.getSiteInternet());
        } else {
            activite = ActiviteMapper.toEntity(activiteDto);
        }
        Activite saved = activiteRepository.save(activite);
        return ActiviteMapper.toDto(saved);
    }


    @Override
    public void deleteActivite(Long id) {
        activiteRepository.deleteById(id);
    }

     @Override
    public ActiviteDto saveActiviteForGuide(ActiviteDto activiteDto, Guide guide) {
        validateActivite(activiteDto, guide.getNombreJours());
        Activite activite = ActiviteMapper.toEntity(activiteDto);
        Activite saved = activiteRepository.save(activite);
        return ActiviteMapper.toDto(saved);
    }

    @Override
    public void validateActivite(ActiviteDto activiteDto, int nombreJours) {
        if (activiteDto.getTitre() == null || activiteDto.getTitre().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le titre de l'activité est obligatoire.");
        if (activiteDto.getDescription() == null || activiteDto.getDescription().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La description de l'activité est obligatoire.");
        if (activiteDto.getCategorie() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La catégorie de l'activité est obligatoire.");
        
        validateActiviteCategorie(activiteDto.getCategorie());
        
        if (activiteDto.getAdresse() == null || activiteDto.getAdresse().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'adresse de l'activité est obligatoire.");
        if (activiteDto.getTelephone() == null || activiteDto.getTelephone().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le téléphone de l'activité est obligatoire.");
        if (activiteDto.getHorairesOuverture() == null || activiteDto.getHorairesOuverture().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les horaires d'ouverture de l'activité sont obligatoires.");
    }

    private void validateActiviteCategorie(String categorie) {
        if (categorie == null) return;
        try {
            ActiviteCategorie.valueOf(categorie);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Catégorie d'activité invalide : " + categorie);
        }
    }
}
