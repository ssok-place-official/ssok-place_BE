package com.example.ssokPlace.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfilePlaceDTO {
    private Long id;
    private String name;
    private String emoji;
    private double lat;
    private double lng;
    private int distanceM;
}
