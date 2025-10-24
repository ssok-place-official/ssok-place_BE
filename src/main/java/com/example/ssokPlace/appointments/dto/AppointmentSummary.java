package com.example.ssokPlace.appointments.dto;

import com.example.ssokPlace.appointments.entity.AppointmentsStatus;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.Instant;

public class AppointmentSummary {
    private final String id;
    private final String title;
    private final Instant startAt;
    private final AppointmentsStatus status;

    @JsonCreator
    public AppointmentSummary(String id, String title, Instant startAt, AppointmentsStatus status) {
        this.id = id;
        this.title = title;
        this.startAt = startAt;
        this.status = status;
    }

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public Instant getStartAt() {
        return startAt;
    }
    public AppointmentsStatus getStatus() {
        return status;
    }
}
