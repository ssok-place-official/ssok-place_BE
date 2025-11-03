package com.example.ssokPlace.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLookupDTO {
    private Long userId;
    private String nickname;
    private boolean isMe;
    private String relation;
}
