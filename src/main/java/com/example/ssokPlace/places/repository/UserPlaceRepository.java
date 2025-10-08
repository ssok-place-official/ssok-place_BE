package com.example.ssokPlace.places.repository;

import com.example.ssokPlace.places.entity.UserPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPlaceRepository extends JpaRepository<UserPlace, Long> {
    Optional<UserPlace> findByUserIdAndPlaceId(Long userId, Long placeId);
}
