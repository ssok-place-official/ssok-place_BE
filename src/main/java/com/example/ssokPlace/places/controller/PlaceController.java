package com.example.ssokPlace.places.controller;

import com.example.ssokPlace.common.CommonResponse;
import com.example.ssokPlace.common.PageDTO;
import com.example.ssokPlace.places.dto.*;
import com.example.ssokPlace.places.service.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @PostMapping
    public CommonResponse<PlaceDTO> create(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody@Valid PlaceCreateReq req
            ) {
        var data = placeService.createOrAttach(principal.getUsername(), req);
        return CommonResponse.created(data, "장소가 저장되었습니다.");
    }

    // 상세 + 인사이트 옵션
    @GetMapping("/{placeId}")
    public CommonResponse<PlaceDTO> detail(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long placeId,
            @RequestParam(defaultValue = "false") boolean includeInsight
    ) {
        var data = placeService.getDetail(principal.getUsername(), placeId, includeInsight);
        return CommonResponse.ok(data, "장소 상세 정보 조회 성공");
    }

    // 지도 중심 근처 저장 / 후보 조회
    @GetMapping("/nearby")
    public CommonResponse<PageDTO<PlacePinDTO>> nearby(
            @AuthenticationPrincipal UserDetails principal,
            @ModelAttribute NearbyQuery q
            ) {
        var data = placeService.nearby(principal.getUsername(), q);
        return CommonResponse.ok(data, "근처 장소 조회 성공");
    }

    // 메모 / 태그 / 상태 수정
    @PatchMapping("/{placeId}")
    public CommonResponse<Object> patch(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long placeId,
            @RequestBody @Valid PlacePatchReq req
            ) {
        var data = placeService.updateUserPlace(principal.getUsername(), placeId, req);
        return CommonResponse.ok(data, "업데이트 성공");
    }
}
