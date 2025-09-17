package com.example.ssokPlace.user.controller;

import com.example.ssokPlace.common.CommonResponse;
import com.example.ssokPlace.user.dto.LoginResDTO;
import com.example.ssokPlace.user.dto.MyInfoDTO;
import com.example.ssokPlace.user.dto.SignupDTO;
import com.example.ssokPlace.user.entity.User;
import com.example.ssokPlace.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signUp")
    public CommonResponse<User> signUp(@RequestBody SignupDTO dto){
        var user = userService.register(dto.getEmail(), dto.getPassword(), dto.getNickname());
        return CommonResponse.created(user, "회원가입 성공");
    }

    @PostMapping("/login")
    public CommonResponse<LoginResDTO> login(@RequestBody LoginResDTO dto){
        var res = userService.loginAndIssueToken(dto.getEmail(), dto.getAccessToken());
        return CommonResponse.ok(res, "로그인 성공");
    }

    @GetMapping("/me")
    public CommonResponse<MyInfoDTO> me(@AuthenticationPrincipal UserDetails principal) {
        // username = email 로 가정
        var u = userService.findByEmail(principal.getUsername());
        var dto = new MyInfoDTO(u.getId(), u.getEmail(), u.getNickname());
        return CommonResponse.ok(dto, "내 정보 조회 성공");
    }

    @PostMapping("/logout")
    public CommonResponse<Void> logout(){
        return CommonResponse.ok(null, "로그아웃 성공");
    }

}
