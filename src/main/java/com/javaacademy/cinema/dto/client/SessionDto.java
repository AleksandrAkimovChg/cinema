package com.javaacademy.cinema.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SessionDto {
    private Integer id;
    @JsonProperty("movie_name")
    private String movieName;
    private String date;
    private BigDecimal price;
}
