package com.example.ssokPlace.places.controller;

import com.example.ssokPlace.common.CommonResponse;
import com.example.ssokPlace.common.PageDTO;
import com.example.ssokPlace.places.dto.*;
import com.example.ssokPlace.places.service.PlaceService;
import com.example.ssokPlace.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final ProfileService profileService;

    @PostMapping
    public CommonResponse<PlaceDTO> saveMyPlace(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody @Valid PlaceCreateReq req
    ) {
        var data = placeService.saveUserPlace(principal.getUsername(), req);
        return CommonResponse.created(data, "장소가 저장되었습니다.");
    }


    @GetMapping("/{placeId}")
    public CommonResponse<PlaceDTO> detail(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long placeId,
            @RequestParam(defaultValue = "false") boolean includeInsight
    ) {
        var data = placeService.getDetail(principal.getUsername(), placeId, includeInsight);
        return CommonResponse.ok(data, "장소 상세 정보 조회 성공");
    }

    @GetMapping("/nearby")
    public CommonResponse<PageDTO<PlacePinDTO>> nearby(
            @AuthenticationPrincipal UserDetails principal,
            @ModelAttribute NearbyQuery q
            ) {
        var data = placeService.nearby(principal.getUsername(), q);
        return CommonResponse.ok(data, "근처 장소 조회 성공");
    }

    @PatchMapping("/{placeId}")
    public CommonResponse<Object> patch(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long placeId,
            @RequestBody @Valid PlacePatchReq req
            ) {
        var data = placeService.updateUserPlace(principal.getUsername(), placeId, req);
        return CommonResponse.ok(data, "업데이트 성공");
    }

    @PatchMapping("/{placeId}/visibility")
    public CommonResponse<Map<String, Object>> updateVisibility(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long placeId,
            @RequestBody VisibilityUpdateRequest body
    ) {
        var vis = profileService.updateVisibility(principal.getUsername(), placeId, body.getVisibility());
        return CommonResponse.ok(
                Map.of("id", placeId, "visibility", vis.name()),
                "장소 공개 범위가 업데이트되었습니다."
        );
    }
}
