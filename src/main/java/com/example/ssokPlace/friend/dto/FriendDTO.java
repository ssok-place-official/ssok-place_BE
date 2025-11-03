package com.example.ssokPlace.friend.dto;

import com.example.ssokPlace.friend.entity.FriendshipStatus;
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
    private FriendshipStatus status;

    public FriendDTO(Long userId, String nickname, FriendshipStatus status) {
        this.userId = userId;
        this.nickname = nickname;
        this.status = status;
    }

    public FriendDTO(Integer userId, String nickname, FriendshipStatus status) {
        this.userId = (userId != null) ? userId.longValue() : null;
        this.nickname = nickname;
        this.status = status;
    }
}