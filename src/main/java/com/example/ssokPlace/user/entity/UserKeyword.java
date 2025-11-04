package com.example.ssokPlace.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_keywords")
@Getter
@NoArgsConstructor
public class UserKeyword {
    @jakarta.persistence.Id
    private Long userId;
    private double weight;
    private OffsetDateTime updatedAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Id implements Serializable{
        private Long userId;
        private String term;
    }
}
