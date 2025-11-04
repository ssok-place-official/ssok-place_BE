package com.example.ssokPlace.places.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@NoArgsConstructor
public class VisibilityUpdateRequest {
    private String visibility;
}
