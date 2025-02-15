package com.javaacademy.cinema.repository.session;

import com.javaacademy.cinema.entity.Session;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {

    Optional<Session> findById(Integer sessionId);

    List<Session> findAll();

    Session save(Session session);
}
