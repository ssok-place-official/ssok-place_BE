package com.example.ssokPlace.places.repository;

import com.example.ssokPlace.places.entity.UserPlace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPlaceRepository extends JpaRepository<UserPlace, Long> {
    Optional<UserPlace> findByUserIdAndPlaceId(Long userId, Long placeId);

    long countByUserId(Long userId);

    @Query("""
      select up from UserPlace up
      where up.userId = :targetUserId
        and (
          up.visibility = com.example.ssokPlace.places.entity.UserPlace.Visibility.PUBLIC
          or (up.visibility = com.example.ssokPlace.places.entity.UserPlace.Visibility.FRIENDS and :isFriend = true)
        )
    """)
    Page<UserPlace> findViewablePlaces(Long targetUserId, boolean isFriend, Pageable pageable);
}
