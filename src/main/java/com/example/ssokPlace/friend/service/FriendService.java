package com.example.ssokPlace.friend.service;

import com.example.ssokPlace.common.PageDTO;
import com.example.ssokPlace.error.ReportableError;
import com.example.ssokPlace.friend.dto.FriendDTO;
import com.example.ssokPlace.friend.dto.FriendRequestDTO;
import com.example.ssokPlace.friend.entity.Friendship;
import com.example.ssokPlace.friend.repository.FriendRepository;
import com.example.ssokPlace.user.entity.User;
import com.example.ssokPlace.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PageDTO<FriendDTO> getFriends(String myEmail, String search, int page, int size) {
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        var pageable = PageRequest.of(page, size);

        var pageResult = friendRepository.findFriendsPaged(me.getId(), search, pageable);

        return PageDTO.of(pageResult);
    }

    /** 친구추가 */
    @Transactional
    public FriendRequestDTO addFriend(String myEmail, Long friendUserId) {
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        if (me.getId().equals(friendUserId)) {
            throw new ReportableError(HttpStatus.BAD_REQUEST, "본인에게 친구 요청을 보낼 수 없습니다.");
        }

        User target = userRepository.findById(friendUserId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "대상 유저를 찾을 수 없습니다."));

        if (friendRepository.existsFriendship(me.getId(), target.getId())) {
            throw new ReportableError(HttpStatus.CONFLICT, "이미 친구 상태입니다.");
        }
        if (friendRepository.existsPendingBetween(me.getId(), target.getId())) {
            throw new ReportableError(HttpStatus.CONFLICT, "이미 친구 요청이 진행 중입니다.");
        }

        User a = me.getId() < target.getId() ? me : target;
        User b = me.getId() < target.getId() ? target : me;

        Friendship req = Friendship.builder()
                .userA(a)
                .userB(b)
                .status("PENDING")
                .createdAt(java.time.OffsetDateTime.now())
                .build();
        friendRepository.save(req);

        return new FriendRequestDTO("PENDING");
    }

}
