package com.example.ssokPlace.appointments.dto;

import com.example.ssokPlace.appointments.entity.AppointmentsStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class AppointmentSummary {
    private final Long id; // publicId
    private final String title;
    private final Instant startAt;
    private final AppointmentsStatus status;

    @JsonCreator
    public AppointmentSummary(
            @JsonProperty("id") Long id,
            @JsonProperty("title") String title,
            @JsonProperty("startAt") Instant startAt,
            @JsonProperty("status") AppointmentsStatus status
    ) {
        this.id = id;
        this.title = title;
        this.startAt = startAt;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Instant getStartAt() { return startAt; }
    public AppointmentsStatus getStatus() { return status; }
}
