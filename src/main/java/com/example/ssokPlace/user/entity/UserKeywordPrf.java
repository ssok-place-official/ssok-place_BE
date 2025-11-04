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
@Getter
@NoArgsConstructor
@Table(name = "user_keyword_prefs")
public class UserKeywordPrf {
    @jakarta.persistence.Id
    private Long userId;
    private String term;
    private boolean pinned;
    private boolean hidden;
    private OffsetDateTime updatedAt;

    public void apply(boolean pinned, boolean hidden){
        this.pinned = pinned;
        this.hidden = hidden;
        this.updatedAt = OffsetDateTime.now();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Id implements Serializable{
        private Long userId;
        private String term;
    }

    public static UserKeywordPrf of(Long userId, String term, boolean pinned, boolean hidden){
        var p = new UserKeywordPrf();
        p.userId = userId;
        p.term = term;
        p.pinned = pinned;
        p.hidden = hidden;
        p.updatedAt = OffsetDateTime.now();
        return p;
    }
}
