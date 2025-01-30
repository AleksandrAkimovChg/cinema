package com.javaacademy.cinema.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class BookingDtoRq {
    @JsonProperty("session_id")
    @NonNull
    private Integer id;
    @JsonProperty("place_name")
    @NonNull
    private String place;
}
