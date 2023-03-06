package com.example.springbootjwtauthentication.exceptions.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Component
public class CustomAuthenticationError implements AuthenticationEntryPoint {

    private static final Logger LOGGER = Logger.getLogger(CustomAuthenticationError.class.getName());

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        LOGGER.warning("Authentication error: " + authException.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("Access denied. Authenticate with valid credentials.");
    }
}
