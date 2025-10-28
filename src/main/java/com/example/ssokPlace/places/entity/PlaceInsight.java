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
    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @OneToOne(optional = false)
    @JoinColumn(name = "place_id", referencedColumnName = "id")
    private Place place;    // ← 일대일 대상은 엔티티

    @Column(length = 8)     // 또는 생략
    private String emoji;

    @Column(columnDefinition = "json")
    private String keywords;

}