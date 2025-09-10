package com.hws.travel.entity;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

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
@ToString(exclude = {"guideActivites", "invitedUsers"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Guide {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    @jakarta.validation.constraints.NotNull
    private String titre;

    @Column(nullable = false)
    @jakarta.validation.constraints.NotNull
    private String description;

    @Column(nullable = false)
    private int nombreJours;

    @ElementCollection
    @jakarta.validation.constraints.NotNull
    private Set<Mobilite> mobilites;

    @ElementCollection
    @jakarta.validation.constraints.NotNull
    private Set<Saison> saisons;

    @ElementCollection
    @jakarta.validation.constraints.NotNull
    private Set<PourQui> pourQui;

    @ManyToMany
    @JoinTable(
        name = "guide_invitation",
        joinColumns = @JoinColumn(name = "guide_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> invitedUsers;

    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GuideActivite> guideActivites;

}
