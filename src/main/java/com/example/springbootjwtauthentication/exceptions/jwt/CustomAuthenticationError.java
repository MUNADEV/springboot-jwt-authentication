package com.example.springbootjwtauthentication.exceptions.jwt;

import io.jsonwebtoken.ExpiredJwtException;
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

        if(authException.getCause() instanceof ExpiredJwtException){
            //response.sendRedirect("/api/auth/login");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access denied.Token expired.");
        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access denied.No habilitated request.");

        }

    }
}
