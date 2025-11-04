package com.example.ssokPlace.profile.controller;

import com.example.ssokPlace.common.CommonResponse;
import com.example.ssokPlace.common.PageDTO;
import com.example.ssokPlace.profile.dto.*;
import com.example.ssokPlace.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    @GetMapping("/me/activity")
    public CommonResponse<ActivityDTO> getMyActivity(
            @RequestParam String myEmail,
            @RequestParam(defaultValue = "30") int lookbackDays
    ) {
        var result = profileService.getMyActivity(myEmail, lookbackDays);
        return CommonResponse.ok(result, "활동 요약 조회 성공");
    }

    @GetMapping("/me/keywords")
    public CommonResponse<KeywordDTO> getMyKeywords(@RequestParam String myEmail) {
        var result = profileService.getMyKeywords(myEmail);
        return CommonResponse.ok(result, "키워드 조회 성공");
    }
    @PatchMapping("/me/keywords")
    public CommonResponse<Void> updateMyKeywords(
            @RequestParam String myEmail,
            @RequestBody KeywordUpdateRequest req
    ) {
        profileService.updateMyKeywords(myEmail, req);
        return CommonResponse.ok(null, "키워드 설정이 업데이트되었습니다.");
    }


    @GetMapping("/{userId}")
    public CommonResponse<ProfileDTO> getUserProfile(
            @RequestParam String myEmail,
            @PathVariable Long userId
    ) {
        var result = profileService.getUserProfile(myEmail, userId);
        return CommonResponse.ok(result, "프로필 조회 성공");
    }

    @GetMapping("/{userId}/places")
    public CommonResponse<PageDTO<ProfilePlaceDTO>> getUserPlaces(
            @RequestParam String myEmail,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var result = profileService.getUserPlaces(myEmail, userId, page, size);
        return CommonResponse.ok(result, "공개 장소 목록 조회 성공");
    }
}