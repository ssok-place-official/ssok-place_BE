package com.example.ssokPlace.places.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlacePinDTO {
    private Long id;
    private String name;
    private Double lat;
    private Double lng;
    private Boolean isClosed;
    private String emoji;
    private Integer distanceM;
}