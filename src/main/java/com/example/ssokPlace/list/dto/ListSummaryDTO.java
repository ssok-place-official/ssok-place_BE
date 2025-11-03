package com.example.ssokPlace.list.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ListSummaryDTO {
    private Long id;
    private String name;
    private String emoji;
    private Long placeCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant updatedAt;
}
