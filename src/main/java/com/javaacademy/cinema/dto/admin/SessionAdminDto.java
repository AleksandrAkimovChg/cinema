package com.javaacademy.cinema.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SessionAdminDto {
    @NonNull
    private String dateTime;
    @NonNull
    private BigDecimal price;
    @NonNull
    private Integer movieId;
}
