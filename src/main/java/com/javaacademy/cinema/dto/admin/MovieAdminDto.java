package com.javaacademy.cinema.dto.admin;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class MovieAdminDto {
    private Integer id;
    @NonNull
    private String name;
    @NonNull
    private String description;
}
