package com.javaacademy.cinema.mapper;

import com.javaacademy.cinema.entity.Place;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlaceMapper {

    public String convertToNamePlace(Place place) {
        return place.getName();
    }

    public List<String> convertToNamePlaces(List<Place> place) {
        return place.stream().map(this::convertToNamePlace).toList();
    }
}
