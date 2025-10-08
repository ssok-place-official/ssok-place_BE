package com.example.ssokPlace.places.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceCreateReq {
    private String name;
    private String address;
    private String naverPlaceId;
    private String placeUrl;

    private Double lat;
    private Double lng;

    @Size(max=2000)
    private String memo;
    private List<@Size(max=20) String> tags;
}
