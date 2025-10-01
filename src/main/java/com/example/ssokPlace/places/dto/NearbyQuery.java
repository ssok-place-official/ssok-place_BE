package com.example.ssokPlace.places.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NearbyQuery {
    private Double centerLat;
    private Double centerLng;
    private Integer radiusM;
    private Integer page;
    private Integer size;
    private Boolean includeClosed;
}
