package com.hws.travel.service;

import com.hws.travel.entity.Activite;
import java.util.List;
import java.util.Optional;

public interface ActiviteService {
    List<Activite> getAllActivites();
    Optional<Activite> getActiviteById(Long id);
    Activite saveActivite(Activite activite);
    void deleteActivite(Long id);
}
