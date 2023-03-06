package com.example.springbootjwtauthentication.security.jwt;

import com.example.springbootjwtauthentication.exceptions.jwt.CustomAccessDenied;
import com.example.springbootjwtauthentication.security.UserDetail;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class RequestFilterJWT  extends OncePerRequestFilter {

    @Autowired
    private UserDetail userDetail;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = getToken(request);
            if (token != null && JwtProvider.validateToken(token)) {
                String username = JwtProvider.getUsernameFromToken(token);
                if (username != null) {
                    UserDetails userDetails = userDetail.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            Logger LOGGER = Logger.getLogger(getClass().getName());
            LOGGER.log(Level.SEVERE, "doFilterInternal Error: ", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

}
