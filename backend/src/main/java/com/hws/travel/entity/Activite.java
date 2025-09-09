package com.hws.travel.entity;

import com.hws.travel.entity.enums.ActiviteCategorie;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    private int jour; // numéro du jour dans le guide
    private int ordre; // ordre de visite dans le jour

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private Guide guide;
}
