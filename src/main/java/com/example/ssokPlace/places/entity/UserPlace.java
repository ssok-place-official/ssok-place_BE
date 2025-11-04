package com.example.ssokPlace.places.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.beans.Visibility;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_places", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "place_id"}))
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserPlace {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Lob
    private String memo;

    @Column(columnDefinition = "json")
    private String tags;

    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PRIVATE;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    private OffsetDateTime lastVisitedAt;

    public enum Visibility{
        PUBLIC, FRIENDS, PRIVATE
    }

    public void updaetMemo(String newMemo){
        this.memo = newMemo;
        this.updatedAt = OffsetDateTime.now();
    }

    public void updateTags(String newTags){
        this.tags = newTags;
        this.updatedAt = OffsetDateTime.now();
    }

    public void markVisitedNow(){
        this.lastVisitedAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }
}
