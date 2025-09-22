package com.example.ssokPlace.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendDTO {
    private Long userId;
    private String handle;
    private String nickname;
    private String avatar;
    private String status; // PENDING | ACCEPTED | BLOCKED


    public FriendDTO(Long userId, String nickname, String status) {
        this.userId = userId;
        this.nickname = nickname;
        this.status = status;
    }

    public FriendDTO(Integer userId, String nickname, String status) {
        this.userId = (userId != null) ? userId.longValue() : null;
        this.nickname = nickname;
        this.status = status;
    }
}