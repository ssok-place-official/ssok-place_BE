package com.example.ssokPlace.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLookupDTO {

    private Long userId;
    private String nickname;
    private String avatarUrl;
    private boolean isMe;
    private String relation;
}
