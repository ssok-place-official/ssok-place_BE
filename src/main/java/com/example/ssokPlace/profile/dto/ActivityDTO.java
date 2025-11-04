package com.example.ssokPlace.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDTO {
    private List<PlaceActivityDTO> frequent;
    private List<PlaceActivityDTO> dormant;
}
