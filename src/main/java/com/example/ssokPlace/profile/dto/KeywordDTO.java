package com.example.ssokPlace.profile.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeywordDTO {
    @JsonProperty("keywords")
    private List<KeywordItem> keywords;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KeywordItem {
        private String term;
        private double weight;
        private boolean pinned;
        private boolean hidden;
    }
}
