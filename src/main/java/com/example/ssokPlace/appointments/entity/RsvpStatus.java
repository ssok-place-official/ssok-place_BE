package com.example.ssokPlace.appointments.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RsvpStatus {
    PENDING, ACCEPTED, DECLINED;

    @JsonCreator
    public static RsvpStatus from(String value) {
        if (value == null) return null;
        return RsvpStatus.valueOf(value.trim().toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return name().toLowerCase(); // 응답에 "accepted"처럼 소문자로 나가게
    }
}
