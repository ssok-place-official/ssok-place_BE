package com.example.ssokPlace.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeywordUpdateRequest {
    private List<String> pin;
    private List<String> unpin;
    private List<String> hide;
    private List<String> unhide;
}
