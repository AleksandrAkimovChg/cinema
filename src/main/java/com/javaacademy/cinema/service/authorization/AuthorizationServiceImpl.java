package com.javaacademy.cinema.service.authorization;

import com.javaacademy.cinema.config.CinemaSecurityProperty;
import com.javaacademy.cinema.exception.SecretTokenCheckFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {
    public static final String SECRET_TOKEN_CHECK_FAILED = "Нет прав на операцию. Обратитесь в службу поддержки.";

    private final CinemaSecurityProperty cinemaSecurityProperty;

    @Override
    public void checkSecurity(Map<String, String> headers) {
        if (!headers.containsKey(cinemaSecurityProperty.getHeader())
                || !isSecurityTokenEquals(headers.get(cinemaSecurityProperty.getHeader()))) {
            throw new SecretTokenCheckFailedException(SECRET_TOKEN_CHECK_FAILED);
        }
    }

    private boolean isSecurityTokenEquals(String userToken) {
        return Objects.equals(userToken, cinemaSecurityProperty.getToken());
    }
}
