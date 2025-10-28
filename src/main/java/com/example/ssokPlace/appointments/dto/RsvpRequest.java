package com.example.ssokPlace.appointments.dto;

import com.example.ssokPlace.appointments.entity.RsvpStatus;
import com.fasterxml.jackson.annotation.JsonCreator;

public class RsvpRequest {
    private final RsvpStatus rsvp;

    @JsonCreator
    public RsvpRequest(RsvpStatus rsvp){
        this.rsvp = rsvp;
    }

    public RsvpStatus getRsvp() {
        return rsvp;
    }
}
