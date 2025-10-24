package com.example.ssokPlace.appointments.dto;

import com.example.ssokPlace.appointments.entity.MemberRole;
import com.example.ssokPlace.appointments.entity.RsvpStatus;
import com.fasterxml.jackson.annotation.JsonCreator;

public class MemberDto {
    private final Long userId;
    private final MemberRole role;
    private final RsvpStatus rsvp;

    @JsonCreator
    public MemberDto(Long userId, MemberRole role, RsvpStatus rsvp) {
        this.userId = userId;
        this.role = role;
        this.rsvp = rsvp;
    }

    public Long getUserId() {
        return userId;
    }
    public MemberRole getRole() {
        return role;
    }
    public RsvpStatus getRsvp() {
        return rsvp;
    }
}
