package com.javaacademy.cinema.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingDtoRs {
    @JsonProperty("ticket_id")
    private Integer ticketId;
    @JsonProperty("place_name")
    private String place;
    @JsonProperty("movie_name")
    private String movieName;
    private String date;
}
