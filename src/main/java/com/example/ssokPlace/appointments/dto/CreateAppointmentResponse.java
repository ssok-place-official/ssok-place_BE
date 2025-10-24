package com.example.ssokPlace.appointments.dto;

import com.example.ssokPlace.appointments.entity.AppointmentsStatus;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

public class CreateAppointmentResponse {
    private final String id;
    private final AppointmentsStatus status;
    private final List<MemberDto> members;

    @JsonCreator
    public CreateAppointmentResponse(String id, AppointmentsStatus status, List<MemberDto> members) {
        this.id = id;
        this.status = status;
        this.members = members;
    }

    public String getId() {
        return id;
    }
    public AppointmentsStatus getStatus() {
        return status;
    }
    public List<MemberDto> getMembers() {
        return members;
    }
}
