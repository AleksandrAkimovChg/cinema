package com.javaacademy.cinema.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SessionDto {
    @Schema(description = "id сеанса", defaultValue = "5")
    private Integer id;
    @Schema(description = "Название фильма", defaultValue = "«V» значит Вендетта")
    @JsonProperty("movie_name")
    private String movieName;
    @Schema(description = "Дата и время сеанса", defaultValue = "01.01.2024 19:00")
    private String date;
    @Schema(description = "Стоимость сеанса", defaultValue = "500.00")
    private BigDecimal price;
}
