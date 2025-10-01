package com.example.ssokPlace.places.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place_insights")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceInsight {

    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Place place;

    @Column(length = 8)
    private String emoji;

    @Column(columnDefinition = "json")
    private String keywords;
}
