package com.example.ssokPlace.friend.entity;

import com.example.ssokPlace.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name="friendships", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "friend_id"}))
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User userB;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private OffsetDateTime createdAt;
}
