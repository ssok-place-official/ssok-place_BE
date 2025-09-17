package com.example.ssokPlace.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyInfoDTO {
    private Long id;
    private String email;
    private String nickname;
}
