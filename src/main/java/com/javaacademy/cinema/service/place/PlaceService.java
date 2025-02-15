package com.javaacademy.cinema.service.place;

import java.util.List;

public interface PlaceService {

    List<String> findFreePlacesOnSession(Integer sessionId);
}
