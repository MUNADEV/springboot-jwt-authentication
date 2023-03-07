package com.example.springbootjwtauthentication.security.jwt;

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
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RequestFilterJWT  extends OncePerRequestFilter {

    @Autowired
    private UserDetail userDetail;

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("doFilter in Response"+response.toString());
        System.out.println("doFilter in Request"+request.toString());
        try {
            String token = getToken(request);
            if (token != null && jwtProvider.validateToken(token)) {
                String email = jwtProvider.getEmailFromToken(token);
                if (email != null) {
                    UserDetails userDetails = userDetail.loadUserByUsername(email);
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
        if (header != null && header.startsWith("Bearer ")) {
           // header =
            //header.split(" ")[1].trim();
            return header.replace("Bearer ", "");
        }
        return null;
    }

}
