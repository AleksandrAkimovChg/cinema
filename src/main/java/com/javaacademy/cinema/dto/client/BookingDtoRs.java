package com.javaacademy.cinema.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingDtoRs {
    @Schema(description = "Номер билета", defaultValue = "25")
    @JsonProperty("ticket_id")
    private Integer ticketId;
    @Schema(description = "Номер места в зале", defaultValue = "А5")
    @JsonProperty("place_name")
    private String place;
    @Schema(description = "Название фильма", defaultValue = "«V» значит Вендетта")
    @JsonProperty("movie_name")
    private String movieName;
    @Schema(description = "Дата и время сеанса", defaultValue = "01.01.2024 19:00")
    private String date;
}
