package com.example.ssokPlace.list.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ListSimpleDTO {
    private Long id;
    private String name;
    private String emoji;
    private Long placeCount;
}
