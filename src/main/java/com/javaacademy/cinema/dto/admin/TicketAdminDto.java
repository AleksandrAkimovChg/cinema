package com.javaacademy.cinema.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketAdminDto {
    @Schema(description = "id билета", defaultValue = "5")
    private Integer id;
    @Schema(description = "id сеанса", defaultValue = "5")
    private Integer session;
    @Schema(description = "Номер места", defaultValue = "А5")
    private String place;
}
