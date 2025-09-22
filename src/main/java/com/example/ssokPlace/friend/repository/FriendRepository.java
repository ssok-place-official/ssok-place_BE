package com.example.ssokPlace.friend.repository;

import com.example.ssokPlace.friend.dto.FriendDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface FriendRepository extends JpaRepository{
    /** 친구 목록 조회 및 검색 */
    List<FriendDTO> findFriends(Long myuserId, String search, Pageable pageable);

    Page<FriendDTO> findFriendsPaged(Long myuserId, String search, Pageable pageable);

    /** 두 사용자 간 친구 관계 조회 */
    boolean existsFriendship(Long userId1, Long userId2);

}
