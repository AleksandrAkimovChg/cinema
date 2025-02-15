package com.javaacademy.cinema.service.authorization;

import java.util.Map;

public interface AuthorizationService {

    void checkSecurity(Map<String, String> headers);
}
