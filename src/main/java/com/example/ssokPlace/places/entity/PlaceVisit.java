package com.example.ssokPlace.places.entity;

import com.example.ssokPlace.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "place_visits")
public class PlaceVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    private OffsetDateTime visitedAt;
    private Integer durationSec;
    private String source;
    private String note;

    private OffsetDateTime createdAt;

    public static PlaceVisit of(User user, Long placeId, OffsetDateTime visitedAt, Integer durationSec, String source, String note){

        var v = new PlaceVisit();
        v.user = user;
        v.placeId = placeId;
        v.visitedAt = visitedAt;
        v.durationSec = durationSec;
        v.source = source;
        v.note = note;
        v.createdAt = OffsetDateTime.now();
        return v;
    }
}
