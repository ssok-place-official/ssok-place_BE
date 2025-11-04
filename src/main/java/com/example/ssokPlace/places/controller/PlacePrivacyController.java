package com.example.ssokPlace.places.controller;

import com.example.ssokPlace.common.CommonResponse;
import com.example.ssokPlace.places.dto.VisibilityUpdateRequest;
import com.example.ssokPlace.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlacePrivacyController {

    private final ProfileService profileService;
    @PatchMapping("/{placeId}")
    public CommonResponse<Map<String, Object>> updateVisibility(
            @RequestParam String myEmail,
            @PathVariable Long placeId,
            @RequestBody VisibilityUpdateRequest body
    ) {
        var vis = profileService.updateVisibility(myEmail, placeId, body.getVisibility());
        return CommonResponse.ok(
                Map.of("id", placeId, "visibility", vis.name()),
                "장소 공개 범위가 업데이트되었습니다."
        );
    }
}
