package com.example.ssokPlace.profile.dto;

import com.example.ssokPlace.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private UserInfo user;
    private List<KeywordInfo> keywords;
    private Stats stats;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private Long id;
        private String nickname;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KeywordInfo {
        private String term;
        private double weight;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Stats {
        private int savedPlaces;
    }
}
