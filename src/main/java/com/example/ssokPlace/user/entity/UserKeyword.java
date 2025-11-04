package com.example.ssokPlace.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_keywords")
@IdClass(UserKeyword.PK.class)
@Getter @NoArgsConstructor
public class UserKeyword {

    @Id private Long userId;
    @Id private String term;

    private double weight;
    private OffsetDateTime updatedAt;

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class PK implements Serializable {
        private Long userId;
        private String term;
    }
}