package com.example.ssokPlace.user.service;

import com.example.ssokPlace.error.ReportableError;
import com.example.ssokPlace.user.component.JwtTokenProvider;
import com.example.ssokPlace.user.dto.LoginResDTO;
import com.example.ssokPlace.user.entity.User;
import com.example.ssokPlace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider tokenProvider;

    public User register(String email, String rawPw, String nickname){
        userRepository.findByEmail(email).ifPresent(u -> {
            throw new ReportableError(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");
        });

        var u = User.builder()
                .email(email)
                .passwordHash(encoder.encode(rawPw))
                .nickname(nickname)
                .build();
        return userRepository.save(u);
    }

    /** 로그인 + AccessToken 발급 */
    public LoginResDTO loginAndIssueToken(String email, String rawPw){
        var u = userRepository.findByEmail(email)
                .orElseThrow(() -> new ReportableError(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!encoder.matches(rawPw, u.getPasswordHash())) {
            throw new ReportableError(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String access = tokenProvider.generateAccessToken(u.getEmail(), u.getId());
        return new LoginResDTO(u.getId(), u.getEmail(), u.getNickname(), access);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."));
    }

    private final Set<String> tokenBlacklist = new HashSet<>();

    public void logout(String token) {
        if (token != null && tokenProvider.validateToken(token)) {
            tokenBlacklist.add(token);
        }
    }
}