package com.example.ssokPlace.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDTO {
    private List<PlaceActivity> frequent;
    private List<PlaceActivity> dormant;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlaceActivity {
        private Long placeId;
        private String name;
        private String emoji;
        private boolean isClosed;
        private double lat;
        private double lng;
        private int distanceM;
    }
}
