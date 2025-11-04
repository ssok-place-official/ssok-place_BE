package com.example.ssokPlace.places.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Column(nullable = false, length = 16)
    @Builder.Default
    private Visibility visibility = Visibility.PRIVATE;

    @Column(nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public enum Visibility {
        PUBLIC, FRIENDS, PRIVATE
    }

    public void changeVisibility(Visibility newVisibility) {
        if (newVisibility == null) {
            throw new IllegalArgumentException("공개 범위는 null일 수 없습니다.");
        }
        this.visibility = newVisibility;
        this.updatedAt = OffsetDateTime.now();
    }

    public static UserPlace of(Long userId, Long placeId, String memo, String tags, Visibility vis) {
        return UserPlace.builder()
                .userId(userId)
                .placeId(placeId)
                .memo(memo)
                .tags(tags)
                .visibility(vis != null ? vis : Visibility.PRIVATE)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }
}
