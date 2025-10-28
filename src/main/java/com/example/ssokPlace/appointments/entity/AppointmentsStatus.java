package com.example.ssokPlace.appointments.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AppointmentsStatus {
    PROPOSED, CONFIRMED, CANCELED;

    @JsonCreator
    public static AppointmentsStatus from(String value) {
        if (value == null) return null;
        return AppointmentsStatus.valueOf(value.trim().toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return name().toLowerCase(); // "proposed"/"confirmed"/"canceled"로 응답
    }
}
