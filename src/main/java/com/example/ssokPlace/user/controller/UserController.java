package com.example.ssokPlace.user.controller;

import com.example.ssokPlace.common.CommonResponse;
import com.example.ssokPlace.user.dto.LoginDTO;
import com.example.ssokPlace.user.dto.LoginResDTO;
import com.example.ssokPlace.user.dto.MyInfoDTO;
import com.example.ssokPlace.user.dto.SignupDTO;
import com.example.ssokPlace.user.entity.User;
import com.example.ssokPlace.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signUp")
    public CommonResponse<MyInfoDTO> signUp(@Valid @RequestBody SignupDTO dto){
        User user = userService.register(dto.getEmail(), dto.getPassword(), dto.getNickname());

        MyInfoDTO body = new MyInfoDTO(user.getId(), user.getEmail(), user.getNickname());
        return CommonResponse.created(body, "회원가입 성공");
    }

    @PostMapping("/login")
    public CommonResponse<LoginResDTO> login(@Valid @RequestBody LoginDTO dto){

        LoginResDTO res = userService.loginAndIssueToken(dto.getEmail(), dto.getPassword());
        return CommonResponse.ok(res, "로그인 성공");
    }

    @GetMapping("/me")
    public CommonResponse<MyInfoDTO> me(@AuthenticationPrincipal UserDetails principal) {

        User u = userService.findByEmail(principal.getUsername());
        MyInfoDTO dto = new MyInfoDTO(u.getId(), u.getEmail(), u.getNickname());
        return CommonResponse.ok(dto, "내 정보 조회 성공");
    }

    @PostMapping("/logout")
    public CommonResponse<Void> logout(@AuthenticationPrincipal UserDetails principal){

        userService.logout(principal != null ? principal.getUsername() : null);
        return CommonResponse.ok(null, "로그아웃 성공");
    }
}
