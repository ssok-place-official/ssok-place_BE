package com.example.ssokPlace.places.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place_meta")
@Getter
@NoArgsConstructor
public class PlaceMeta {
    @Id
    private Long id; // placeId (네이버에서 가져온 키)

    @Column
    private String name;
    private String emoji;

    private Double lat;
    private Double lng;
    private String thumbnailUrl;

    public PlaceMeta(Long id, String name, String emoji, Double lat, Double lng, String thumbnailUrl) {
        this.id = id;
        this.name = name;
        this.emoji = emoji;
        this.lat = lat;
        this.lng = lng;
        this.thumbnailUrl = thumbnailUrl;
    }
}
