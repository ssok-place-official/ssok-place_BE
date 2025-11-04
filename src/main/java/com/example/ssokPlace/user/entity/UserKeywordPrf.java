package com.example.ssokPlace.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "user_keyword_prefs")
@IdClass(UserKeywordPrf.PK.class)
@Getter @NoArgsConstructor
public class UserKeywordPrf {

    @Id private Long userId;
    @Id private String term;

    private boolean pinned;
    private boolean hidden;

    @Getter @NoArgsConstructor @AllArgsConstructor
    public static class PK implements Serializable {
        private Long userId;
        private String term;
    }

    public static UserKeywordPrf of(Long userId, String term, boolean pinned, boolean hidden) {
        UserKeywordPrf e = new UserKeywordPrf();
        e.userId = userId;
        e.term   = term;
        e.pinned = pinned;
        e.hidden = hidden;
        return e;
    }

    public void apply(boolean pinned, boolean hidden){
        this.pinned = pinned;
        this.hidden = hidden;
    }
}

