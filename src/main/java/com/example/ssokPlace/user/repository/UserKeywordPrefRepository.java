package com.example.ssokPlace.user.repository;

import com.example.ssokPlace.user.entity.UserKeywordPrf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserKeywordPrefRepository extends JpaRepository<UserKeywordPrf, UserKeywordPrf.Id> {
    List<UserKeywordPrf> findByUserId(Long userId);

    List<UserKeywordPrf> findByIdUserId(Long userId);
}
