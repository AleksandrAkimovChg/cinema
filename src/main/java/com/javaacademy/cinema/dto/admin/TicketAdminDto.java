package com.javaacademy.cinema.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketAdminDto {
    private Integer id;
    private Integer session;
    private String place;
    private boolean isSold;
}
