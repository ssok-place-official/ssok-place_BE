package com.example.ssokPlace.places.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceDTO {
    private Long id;
    private String name;
    private String address;
    private Double lat;
    private Double lng;
    private Boolean isClosed;

    private String memo;
    private List<String> tags;
    private Instant createdAt;

    private InsightDTO insight;

}