package com.javaacademy.cinema.service.session;

import com.javaacademy.cinema.dto.admin.SessionAdminDto;
import com.javaacademy.cinema.dto.client.SessionDto;

import java.util.List;

public interface SessionService {

    void createSession(SessionAdminDto dto);

    List<SessionDto> findAllSessions();
}
