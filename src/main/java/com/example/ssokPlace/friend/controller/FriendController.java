package com.example.ssokPlace.friend.controller;

import com.example.ssokPlace.common.CommonResponse;
import com.example.ssokPlace.common.PageDTO;
import com.example.ssokPlace.friend.dto.FriendAddDTO;
import com.example.ssokPlace.friend.dto.FriendDTO;
import com.example.ssokPlace.friend.dto.FriendRequestDTO;
import com.example.ssokPlace.friend.dto.FriendSettingPatchDTO;
import com.example.ssokPlace.friend.service.FriendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    public CommonResponse<PageDTO<FriendDTO>> list(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        var data = friendService.getFriends(principal.getUsername(), search, page, size);
        return CommonResponse.ok(data, "친구 목록 조회 성공");
    }

    @PostMapping("/add")
    public CommonResponse<FriendRequestDTO> add(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody @Valid FriendAddDTO friendAddDTO
    ) {
        var data = friendService.addFriend(principal.getUsername(), friendAddDTO.getFriendUserId());
        return CommonResponse.ok(data, "친구 요청 전송 성공");
    }

    @PostMapping("/respond")
    public CommonResponse<FriendRequestDTO> respond(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam Long friendUserId,
            @RequestParam boolean accept
    ) {
        var data = friendService.respondToRequest(principal.getUsername(), friendUserId, accept);
        return CommonResponse.ok(data, accept ? "친구 요청 응답 성공" : "친구 요청 거절 성공");
    }

    @GetMapping("/requests")
    public CommonResponse<List<FriendDTO>> pendingRequests(
            @AuthenticationPrincipal UserDetails principal
    ) {
        var data = friendService.getPendingRequests(principal.getUsername());
        return CommonResponse.ok(data, "친구 요청 목록 조회 성공");
    }

    @PatchMapping("/{friendUserId}")
    public ResponseEntity<CommonResponse<Map<String, Object>>> patchPinned(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long friendUserId,
            @RequestBody FriendSettingPatchDTO body
    ) {
        friendService.updatePinned(principal.getUsername(), friendUserId, body.getPinned());
        return ResponseEntity.ok(CommonResponse.ok(
                Map.of("userId", friendUserId, "pinned", body.getPinned()),
                "친구 설정이 업데이트되었습니다."));
    }

    @PatchMapping("/{friendUserId}/mute")
    public ResponseEntity<CommonResponse<Map<String, Object>>> patchMuted(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long friendUserId,
            @RequestBody FriendSettingPatchDTO body
    ) {
        friendService.updateMuted(principal.getUsername(), friendUserId, body.getMuted());
        return ResponseEntity.ok(CommonResponse.ok(
                Map.of("userId", friendUserId, "muted", body.getMuted()),
                "뮤트 설정이 업데이트되었습니다."));
    }

    @PatchMapping("/{friendUserId}/sharing")
    public ResponseEntity<CommonResponse<Map<String, Object>>> patchSharing(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long friendUserId,
            @RequestBody FriendSettingPatchDTO body
    ) {
        friendService.updateSharing(principal.getUsername(), friendUserId, body.getShareMyPlaces());
        return ResponseEntity.ok(CommonResponse.ok(
                Map.of("userId", friendUserId, "shareMyPlaces", body.getShareMyPlaces()),
                "공유 설정이 업데이트되었습니다."));
    }

    @DeleteMapping("/{friendUserId}")
    public ResponseEntity<CommonResponse<Map<String, Object>>> deleteFriend(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long friendUserId
    ) {
        friendService.deleteFriend(principal.getUsername(), friendUserId);
        return ResponseEntity.ok(CommonResponse.ok(
                Map.of("userId", friendUserId),
                "친구가 삭제되었습니다."));
    }
}
