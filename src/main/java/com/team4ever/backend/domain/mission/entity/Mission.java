package com.team4ever.backend.domain.mission.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "missions")
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private MissionType type;

    private int targetCount;
    private int rewardPoint;
    private LocalDate completeAt;
    private String imageUrl;
}

