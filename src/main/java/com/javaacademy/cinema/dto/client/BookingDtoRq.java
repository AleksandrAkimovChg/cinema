package com.javaacademy.cinema.dto.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

@Data
public class BookingDtoRq {
    @JsonProperty(value = "session_id", required = true)
    private Integer id;
    @JsonProperty(value = "place_name", required = true)
    private String place;

    @JsonCreator
    public BookingDtoRq(@NonNull Integer id, @NonNull String place) {
        this.id = id;
        this.place = place;
    }
}
