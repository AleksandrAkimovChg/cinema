package com.javaacademy.cinema.dto.admin;

import com.javaacademy.cinema.entity.Place;
import com.javaacademy.cinema.entity.Session;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketAdminDto {
    private Integer id;
    private Session session;
    private Place place;
    private boolean isSold;
}
