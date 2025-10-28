package com.example.ssokPlace.appointments.dto;

import com.example.ssokPlace.appointments.entity.AppointmentsStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CreateAppointmentResponse {
    private final Long id;
    private final AppointmentsStatus status;
    private final List<MemberDto> members;

    @JsonCreator
    public CreateAppointmentResponse(
            @JsonProperty("id") Long id,
            @JsonProperty("status") AppointmentsStatus status,
            @JsonProperty("members") List<MemberDto> members
    ) {
        this.id = id;
        this.status = status;
        this.members = members;
    }

    public Long getId() { return id; }
    public AppointmentsStatus getStatus() { return status; }
    public List<MemberDto> getMembers() { return members; }
}