package com.example.ssokPlace.places.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_places", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "place_id"}))
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserPlace {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Lob
    private String memo;

    @Column(columnDefinition = "json")
    private String tags;
}
