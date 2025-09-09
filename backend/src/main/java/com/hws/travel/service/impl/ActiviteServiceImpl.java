package com.hws.travel.service.impl;

import com.hws.travel.entity.Activite;
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
    public List<Activite> getAllActivites() {
        return activiteRepository.findAll();
    }

    @Override
    public Optional<Activite> getActiviteById(Long id) {
        return activiteRepository.findById(id);
    }

    @Override
    public Activite saveActivite(Activite activite) {
        return activiteRepository.save(activite);
    }

    @Override
    public void deleteActivite(Long id) {
        activiteRepository.deleteById(id);
    }
}
