package com.hws.travel.entity;

import jakarta.persistence.Column;

import lombok.*;

import java.util.List;

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
import jakarta.validation.constraints.NotNull;

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
    @NotNull
    private String titre;

    @Column(nullable = false)
    @NotNull
    private String description;

    @Column(nullable = false)
    private int nombreJours;

    @ElementCollection
    @NotNull
    private List<Mobilite> mobilites;

    @ElementCollection
    @NotNull
    private List<Saison> saisons;

    @ElementCollection
    @NotNull
    private List<PourQui> pourQui;

    @ManyToMany
    @JoinTable(
        name = "guide_invitation",
        joinColumns = @JoinColumn(name = "guide_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> invitedUsers;

    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuideActivite> guideActivites;

}
