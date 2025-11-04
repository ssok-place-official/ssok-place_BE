package com.example.ssokPlace.user.repository;

import com.example.ssokPlace.user.entity.UserKeyword;
import com.example.ssokPlace.user.entity.UserKeywordPrf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserKeywordRepository extends JpaRepository<UserKeyword, UserKeywordPrf.Id> {
    List<UserKeyword> findTop100ByUserIdOrderByWeightDesc(Long userId);

    List<UserKeyword> findTop10ByIdUserIdOrderByWeightDesc(Long userId);
}