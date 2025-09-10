package com.hws.travel.entity;

import java.util.Set;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Activite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    @NotNull
    private String titre;

    @Column(nullable = false)
    @NotNull
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private ActiviteCategorie categorie;

    @Column(nullable = false)
    @NotNull
    private String adresse;

    @Column(nullable = false)
    @NotNull
    private String telephone;

    @Column(nullable = false)
    @NotNull
    private String horairesOuverture;

    private String siteInternet;

    @OneToMany(mappedBy = "activite", cascade = CascadeType.ALL)
    private Set<GuideActivite> guideActivites;
}
