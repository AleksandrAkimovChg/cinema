package com.javaacademy.cinema.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SessionAdminDto {
    @Schema(description = "Дата и время сеанса", defaultValue = "01.01.2024 19:00")
    @NonNull
    private String dateTime;
    @Schema(description = "Стоимость сеанса", defaultValue = "500.00")
    @NonNull
    private BigDecimal price;
    @Schema(description = "id фильма", defaultValue = "5")
    @NonNull
    private Integer movieId;
}
