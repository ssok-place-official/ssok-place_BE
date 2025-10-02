package com.example.ssokPlace.places.dto;

import lombok.*;

@Getter
@Builder
@Data
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
