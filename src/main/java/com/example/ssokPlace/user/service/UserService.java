package com.example.ssokPlace.user.service;

import com.example.ssokPlace.user.component.JwtTokenProvider;
import com.example.ssokPlace.user.dto.LoginResDTO;
import com.example.ssokPlace.user.entity.User;
import com.example.ssokPlace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider tokenProvider;

    public User register(String email, String rawPw, String nickname){
        userRepository.findByEmail(email)
                .ifPresent(u -> { throw new IllegalStateException("이미 존재하는 이메일입니다."); });
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
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));
        if (!encoder.matches(rawPw, u.getPasswordHash())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }
        String access = tokenProvider.generateAccessToken(u.getEmail(), u.getId());
        return new LoginResDTO(u.getId(), u.getEmail(), u.getNickname(), access);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));
    }

}