package com.example.ssokPlace.appointments.dto;

import com.example.ssokPlace.appointments.entity.AppointmentsStatus;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.Instant;
import java.util.List;

public class AppointmentDetailResponse {
    private final String id;
    private final String title;
    private final String placeId;
    private final Instant startAt;
    private final AppointmentsStatus status;
    private final List<MemberDto> members;

    @JsonCreator
    public AppointmentDetailResponse(String id, String title, String placeId, Instant startAt, AppointmentsStatus status, List<MemberDto> members) {
        this.id = id;
        this.title = title;
        this.placeId = placeId;
        this.startAt = startAt;
        this.status = status;
        this.members = members;
    }

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getPlaceId() {
        return placeId;
    }
    public Instant getStartAt() {
        return startAt;
    }
    public AppointmentsStatus getStatus() {
        return status;
    }
    public List<MemberDto> getMembers() {
        return members;
    }
}
