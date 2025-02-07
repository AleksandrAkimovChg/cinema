package com.javaacademy.cinema.dto.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

@Data
public class BookingDtoRq {
    @JsonProperty(value = "session_id", required = true)
    private Integer sessionId;
    @JsonProperty(value = "place_name", required = true)
    private String place;

    @JsonCreator
    public BookingDtoRq(@NonNull Integer sessionId, @NonNull String place) {
        this.sessionId = sessionId;
        this.place = place;
    }
}
