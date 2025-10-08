package com.example.ssokPlace.places.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsightDTO {
    private String emoji;
    private List<Map<String, Object>> keywords;
}
