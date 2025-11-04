package com.example.ssokPlace.user.repository;

import com.example.ssokPlace.user.entity.UserKeywordPrf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserKeywordPrefRepository extends JpaRepository<UserKeywordPrf, UserKeywordPrf.PK> {
    List<UserKeywordPrf> findByUserId(Long userId);
    Optional<UserKeywordPrf> findByUserIdAndTerm(Long userId, String term);
}