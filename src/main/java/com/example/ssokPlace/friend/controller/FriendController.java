package com.example.ssokPlace.friend.controller;

import com.example.ssokPlace.common.CommonResponse;
import com.example.ssokPlace.common.PageDTO;
import com.example.ssokPlace.friend.dto.FriendAddDTO;
import com.example.ssokPlace.friend.dto.FriendDTO;
import com.example.ssokPlace.friend.dto.FriendRequestDTO;
import com.example.ssokPlace.friend.service.FriendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
