package com.javaacademy.cinema.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private Integer id;
    private Session session;
    private Place place;
    private boolean isSold;

    public Ticket(Session session, Place place) {
        this.session = session;
        this.place = place;
    }
}
