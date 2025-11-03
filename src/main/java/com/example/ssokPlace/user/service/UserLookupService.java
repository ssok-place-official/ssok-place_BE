package com.example.ssokPlace.user.service;

import com.example.ssokPlace.error.ReportableError;
import com.example.ssokPlace.friend.entity.Friendship;
import com.example.ssokPlace.friend.repository.FriendRepository;
import com.example.ssokPlace.user.dto.UserLookupDTO;
import com.example.ssokPlace.user.entity.User;
import com.example.ssokPlace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLookupService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    public UserLookupDTO lookup(String myEmail, Long targetUserId) {
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."));

        if (me.getId().equals(target.getId())) {
            return new UserLookupDTO(target.getId(), target.getNickname(),true, "NONE");
        }

        String relation = "NONE";
        var opt = friendRepository.findBetweenUsers(me.getId(), target.getId());
        if (opt.isPresent()) {
            Friendship f = opt.get();
            relation = switch (f.getStatus()) {
                case ACCEPTED -> "FRIEND";
                case PENDING  -> "PENDING";
                case BLOCKED  -> "BLOCKED";
                default       -> "NONE";
            };
        }

        return new UserLookupDTO(
                target.getId(),
                target.getNickname(),
                false,
                relation
        );
    }
}
