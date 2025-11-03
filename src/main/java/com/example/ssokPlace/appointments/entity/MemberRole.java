package com.example.ssokPlace.appointments.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MemberRole {
    HOST, GUEST;

    @JsonCreator
    public static MemberRole from(String value) {
        if (value == null) return null;
        return MemberRole.valueOf(value.trim().toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return name().toLowerCase(); // "host"/"guest"로 응답
    }
}
