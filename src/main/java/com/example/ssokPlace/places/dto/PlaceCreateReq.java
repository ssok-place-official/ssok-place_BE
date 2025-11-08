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
    private Long placeId;
    private String memo;
    private List<String> tags;
}
