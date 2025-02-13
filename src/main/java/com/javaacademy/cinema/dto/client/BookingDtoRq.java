package com.javaacademy.cinema.dto.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

@Data
public class BookingDtoRq {
    @Schema(description = "id сеанса", defaultValue = "5")
    @JsonProperty(value = "session_id", required = true)
    private Integer sessionId;
    @Schema(description = "Номер фильма", defaultValue = "А5")
    @JsonProperty(value = "place_name", required = true)
    private String place;

    @JsonCreator
    public BookingDtoRq(@NonNull Integer sessionId, @NonNull String place) {
        this.sessionId = sessionId;
        this.place = place;
    }
}
