package com.example.ssokPlace.friend.dto;

import lombok.Data;

@Data
public class FriendSettingPatchDTO {
    private Boolean pinned;
    private Boolean muted;
    private Boolean ShareMyPlaces;
}
