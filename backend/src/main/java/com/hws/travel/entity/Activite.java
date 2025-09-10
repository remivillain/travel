package com.hws.travel.entity;

import java.util.Set;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

import com.hws.travel.entity.enums.ActiviteCategorie;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;

    @Enumerated(EnumType.STRING)
    private ActiviteCategorie categorie;

    private String adresse;
    private String telephone;
    private String horairesOuverture;
    private String siteInternet;

    @OneToMany(mappedBy = "activite", cascade = CascadeType.ALL)
    private Set<GuideActivite> guideActivites;
}
