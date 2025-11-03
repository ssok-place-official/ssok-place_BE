package com.example.ssokPlace.list.dto;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class CreateListRequest {
    private String name;
    private String emoji;
}
