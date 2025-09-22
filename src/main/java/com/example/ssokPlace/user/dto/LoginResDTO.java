package com.example.ssokPlace.user.dto;

import lombok.Data;

@Data
public class LoginResDTO {
    private Long userId;
    private String email;
    private String nickname;
    private String accessToken;

    public LoginResDTO(Long userId, String email, String nickname, String accessToken) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.accessToken = accessToken;
    }
}
