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
    private FriendshipStatus status;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column(nullable = false) private boolean aPinned;
    @Column(nullable = false) private boolean bPinned;

    @Column(nullable = false) private boolean aMuted;
    @Column(nullable = false) private boolean bMuted;

    @Column(nullable = false) private boolean aShareMyPlaces;
    @Column(nullable = false) private boolean bShareMyPlaces;

    public static Friendship pendingOf(User u1, User u2) {
        final User a = u1.getId() < u2.getId() ? u1 : u2;
        final User b = u1.getId() < u2.getId() ? u2 : u1;

        Friendship f = new Friendship();
        f.userA = a;
        f.userB = b;
        f.status = FriendshipStatus.PENDING;
        f.createdAt = OffsetDateTime.now();
        f.updatedAt = f.createdAt;
        f.aPinned = false; f.bPinned = false;
        f.aMuted  = false; f.bMuted  = false;
        f.aShareMyPlaces = false; f.bShareMyPlaces = false;
        return f;
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public void accept() {
        ensurePending();
        this.status = FriendshipStatus.ACCEPTED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void reject() {
        ensurePending();
        this.status = FriendshipStatus.REJECTED;
        this.updatedAt = OffsetDateTime.now();
    }

    public void markPinnedFor(Long viewerId, boolean pinned) {
        if (viewerId.equals(userA.getId())) this.aPinned = pinned;
        else if (viewerId.equals(userB.getId())) this.bPinned = pinned;
        else throw new IllegalArgumentException("뷰어는 참여자가 아님.");
        this.updatedAt = OffsetDateTime.now();
    }

    public void markMutedFor(Long viewerId, boolean muted) {
        if (viewerId.equals(userA.getId())) this.aMuted = muted;
        else if (viewerId.equals(userB.getId())) this.bMuted = muted;
        else throw new IllegalArgumentException("뷰어는 참여자가 아님.");
        this.updatedAt = OffsetDateTime.now();
    }

    public void setShareMyPlacesFor(Long ownerId, boolean share) {
        if (ownerId.equals(userA.getId())) this.aShareMyPlaces = share;
        else if (ownerId.equals(userB.getId())) this.bShareMyPlaces = share;
        else throw new IllegalArgumentException("호스트가 참여자가 아님.");
        this.updatedAt = OffsetDateTime.now();
    }

    public User otherOf(Long meId) {
        return meId.equals(userA.getId()) ? userB : userA;
    }

    private void ensurePending() {
        if (this.status != FriendshipStatus.PENDING) {
            throw new IllegalStateException("요청이 이미 진행된 상태입니다.");
        }
    }
}
