package com.example.ssokPlace.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends JpaRepository {
    boolean existPendingBetween(Long userId1, Long userId2);
    void createPending(Long fromUserId, Long toUserId);
}
