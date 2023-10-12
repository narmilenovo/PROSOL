package com.example.sales_otherservice.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;

@Aspect
@Component
public class RoleAuthorizationAspect {

    @Before("@annotation(hasRole)")
    public void checkRole(JoinPoint joinPoint, hasRole hasRole) throws AccessDeniedException {
        // Get the HttpServletRequest from the method arguments
        HttpServletRequest request = getHttpServletRequest(joinPoint.getArgs());

        if (request != null) {
            String rolesHeaderValue = request.getHeader("X-User-Roles");
            if (rolesHeaderValue != null) {
                // Split the roles separated by a delimiter (e.g., comma)
                String[] roles = rolesHeaderValue.split(",");

                // Automatically add the "ROLE_" prefix
                String requiredRole = "ROLE_" + hasRole.value();

                if (!Arrays.asList(roles).contains(requiredRole)) {
                    // Access is denied
                    throw new AccessDeniedException("Access Denied");
                }
            }
        }
    }

    private HttpServletRequest getHttpServletRequest(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest) {
                return (HttpServletRequest) arg;
            }
        }
        return null;
    }
}
