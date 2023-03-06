package com.example.springbootjwtauthentication.exceptions.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class CustomAccessDenied implements AccessDeniedHandler {
    private static final Logger LOGGER = Logger.getLogger(CustomAccessDenied.class.getName());

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        LOGGER.warning("Access denied exception: " + accessDeniedException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User does not have permission to access the requested resource: " + request.getRequestURI());
    }
}
