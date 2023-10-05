package com.example.user_management.service;

import com.example.user_management.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import static com.example.user_management.utils.Constants.HEADER_STRING;
import static com.example.user_management.utils.Constants.TOKEN_PREFIX;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader(HEADER_STRING);
        final String jwt;
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            return;
        }
        jwt = authHeader.substring(7);
        var storedToken = tokenRepository.findByTokenValue(jwt)
                .orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
//            tokenRepository.save(storedToken);
            tokenRepository.delete(storedToken);
            SecurityContextHolder.clearContext();
        }
    }
}
