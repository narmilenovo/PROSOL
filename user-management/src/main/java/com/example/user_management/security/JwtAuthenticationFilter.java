package com.example.user_management.security;

import static com.example.user_management.utils.Constants.HEADER_STRING;
import static com.example.user_management.utils.Constants.JWT_EXPIRED_MESSAGE;
import static com.example.user_management.utils.Constants.JWT_ILLEGAL_ARGUMENT_MESSAGE;
import static com.example.user_management.utils.Constants.JWT_SIGNATURE_MESSAGE;
import static com.example.user_management.utils.Constants.TOKEN_PREFIX;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.user_management.repository.TokenRepository;
import com.example.user_management.service.JwtService;
import com.example.user_management.service.UserDetailsServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HEADER_STRING);
        String jwt = null;
        String userEmail = null;

        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            jwt = authHeader.replace(TOKEN_PREFIX, "");

            try {
                userEmail = jwtService.extractUsername(jwt);

            } catch (IllegalArgumentException e) {
                logger.error(JWT_ILLEGAL_ARGUMENT_MESSAGE, e);
            } catch (ExpiredJwtException e) {
                logger.warn(JWT_EXPIRED_MESSAGE, e);
            } catch (JwtException e) {
                logger.error(JWT_SIGNATURE_MESSAGE);
            }
        } else {
            logger.warn("couldn't find bearer string, will ignore the header");

        }
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            var isTokenValid = tokenRepository.findByTokenValue(jwt)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            boolean validateToken = jwtService.isTokenValid(jwt, userDetails) && isTokenValid;

            if (Boolean.TRUE.equals(validateToken)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info("Authenticated email:-----> " + userEmail);

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
