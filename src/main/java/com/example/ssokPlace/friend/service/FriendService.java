package com.example.ssokPlace.friend.service;

import com.example.ssokPlace.common.PageDTO;
import com.example.ssokPlace.error.ReportableError;
import com.example.ssokPlace.friend.dto.FriendDTO;
import com.example.ssokPlace.friend.dto.FriendRequestDTO;
import com.example.ssokPlace.friend.entity.Friendship;
import com.example.ssokPlace.friend.entity.FriendshipStatus;
import com.example.ssokPlace.friend.repository.FriendRepository;
import com.example.ssokPlace.user.entity.User;
import com.example.ssokPlace.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Transactional
    public PageDTO<FriendDTO> getFriends(String myEmail, String search, int page, int size) {
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        var pageable = PageRequest.of(page, size);
        var pageResult = friendRepository.findFriendsPaged(me.getId(), search, pageable);
        return PageDTO.of(pageResult);
    }

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

        Friendship req = Friendship.pendingOf(me, target);
        friendRepository.save(req);

        return new FriendRequestDTO(req.getStatus().name());
    }

    @Transactional
    public FriendRequestDTO respondToRequest(String myEmail, Long friendUserId, boolean accept) {
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));
        Friendship friendship = friendRepository.findBetweenUsers(me.getId(), friendUserId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "친구 요청이 존재하지 않습니다."));

        if (accept) friendship.accept();
        else friendship.reject();

        return new FriendRequestDTO(friendship.getStatus().name());
    }

    @Transactional
    public List<FriendDTO> getPendingRequests(String myEmail) {
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));

        List<Friendship> requests = friendRepository.findPendingRequests(me.getId());

        return requests.stream()
                .map(f -> {
                    User other = f.otherOf(me.getId());
                    return new FriendDTO(other.getId(), other.getNickname(), f.getStatus());
                })
                .toList();
    }

    @Transactional
    public void updatePinned(String myEmail, Long friendUserId, Boolean pinned) {
        if (pinned == null) {
            throw new ReportableError(HttpStatus.BAD_REQUEST, "pinned 값이 필요합니다.");
        }
        Friendship f = getAcceptedFriendshipOr404(myEmail, friendUserId);
        Long meId = userRepository.findByEmail(myEmail).orElseThrow().getId();
        f.markPinnedFor(meId, pinned);
    }

    @Transactional
    public void updateMuted(String myEmail, Long friendUserId, Boolean muted) {
        if (muted == null) {
            throw new ReportableError(HttpStatus.BAD_REQUEST, "muted 값이 필요합니다.");
        }
        Friendship f = getAcceptedFriendshipOr404(myEmail, friendUserId);
        Long meId = userRepository.findByEmail(myEmail).orElseThrow().getId();
        f.markMutedFor(meId, muted);
    }

    @Transactional
    public void updateSharing(String myEmail, Long friendUserId, Boolean shareMyPlaces) {
        if (shareMyPlaces == null) {
            throw new ReportableError(HttpStatus.BAD_REQUEST, "shareMyPlaces 값이 필요합니다.");
        }
        Friendship f = getAcceptedFriendshipOr404(myEmail, friendUserId);
        Long meId = userRepository.findByEmail(myEmail).orElseThrow().getId();
        f.setShareMyPlacesFor(meId, shareMyPlaces);
    }

    @Transactional
    public void deleteFriend(String myEmail, Long friendUserId) {
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));
        Friendship f = friendRepository.findBetweenUsers(me.getId(), friendUserId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "해당 친구를 찾을 수 없습니다."));
        friendRepository.delete(f);
    }

    private Friendship getAcceptedFriendshipOr404(String myEmail, Long friendUserId) {
        User me = userRepository.findByEmail(myEmail)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."));
        Friendship f = friendRepository.findBetweenUsers(me.getId(), friendUserId)
                .orElseThrow(() -> new ReportableError(HttpStatus.NOT_FOUND, "해당 친구를 찾을 수 없습니다."));
        if (f.getStatus() != FriendshipStatus.ACCEPTED) {
            throw new ReportableError(HttpStatus.BAD_REQUEST, "친구로 수락된 상태가 아닙니다.");
        }
        return f;
    }

}
