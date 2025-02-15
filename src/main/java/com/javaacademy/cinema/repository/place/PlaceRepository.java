package com.javaacademy.cinema.repository.place;

import com.javaacademy.cinema.entity.Place;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository {

    Optional<Place> findById(Integer placeId);

    List<Place> findAll();
}
