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
public class RoleAuthorizationsAspect {

    @Before("@annotation(hasAnyRole)")
    public void checkRoles(JoinPoint joinPoint, hasAnyRole hasAnyRole) throws AccessDeniedException {
        HttpServletRequest request = getHttpServletRequest(joinPoint.getArgs());

        if (request != null) {
            String rolesHeaderValue = request.getHeader("X-User-Roles");
            if (rolesHeaderValue != null) {
                String[] roles = rolesHeaderValue.split(",");

                // Automatically add the "ROLE_" prefix and check if any of the specified roles are present
                for (String requiredRole : hasAnyRole.value().split(",")) {
                    if (Arrays.asList(roles).contains("ROLE_" + requiredRole)) {
                        return; // Access is granted
                    }
                }
            }
        }

        // Access is denied
        throw new AccessDeniedException("Access Denied");
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
