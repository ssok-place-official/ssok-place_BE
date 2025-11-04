package com.example.ssokPlace.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceActivityDTO {
    private Long placeId;
    private String name;
    private String emoji;
    private boolean isClosed;
    private double lat;
    private double lng;
    private int distanceM;
}
