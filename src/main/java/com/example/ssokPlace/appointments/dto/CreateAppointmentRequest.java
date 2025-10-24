package com.example.ssokPlace.appointments.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public final class CreateAppointmentRequest {
    private final String title;
    private final String note;
    private final String placeId;
    private final Instant startAt;
    private final java.util.List<Long> memberUserIds;

    @JsonCreator
    public CreateAppointmentRequest(
            @JsonProperty("title") String title,
            @JsonProperty("note") String note,
            @JsonProperty("placeId") String placeId,
            @JsonProperty("startAt") Instant startAt,
            @JsonProperty("memberUserIds") java.util.List<Long> memberUserIds
    ) {
        this.title = title;
        this.note = note;
        this.placeId = placeId;
        this.startAt = startAt;
        this.memberUserIds = (memberUserIds == null) ? java.util.List.of() : java.util.List.copyOf(memberUserIds);
    }

    public String getTitle() { return title; }
    public String getNote() { return note; }
    public String getPlaceId() { return placeId; }
    public Instant getStartAt() { return startAt; }
    public java.util.List<Long> getMemberUserIds() { return memberUserIds; }
}