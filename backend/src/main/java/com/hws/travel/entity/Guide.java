package com.hws.travel.entity;

import lombok.*;
import java.util.Set;

import com.hws.travel.entity.enums.Mobilite;
import com.hws.travel.entity.enums.PourQui;
import com.hws.travel.entity.enums.Saison;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;
    private int nombreJours;

    @ElementCollection
    private Set<Mobilite> mobilites;

    @ElementCollection
    private Set<Saison> saisons;

    @ElementCollection
    private Set<PourQui> pourQui;

    @ManyToMany
    @JoinTable(
        name = "guide_invitation",
        joinColumns = @JoinColumn(name = "guide_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> invitedUsers;

    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL)
    private Set<Activite> activites;

}
