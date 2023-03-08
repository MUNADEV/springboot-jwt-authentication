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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RequestFilterJWT  extends OncePerRequestFilter {

    @Autowired
    private UserDetail userDetail;

    @Autowired
    private JwtProvider jwtProvider;

    /**
     * This method filters incoming requests and processes JWT authentication.
     * If the token is valid, it sets the authentication in the security context.
     * @param request the HTTP servlet request
     * @param response the HTTP servlet response
     * @param filterChain the filter chain to execute
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = getToken(request);
            String email = jwtProvider.getEmailFromToken(token);

            if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = userDetail.loadUserByUsername(email);
                if (token != null && jwtProvider.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            Logger LOGGER = Logger.getLogger(getClass().getName());
            LOGGER.log(Level.SEVERE, "doFilterInternal Error: ", e);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * This method retrieves the token from the request header.
     * @param request the HTTP servlet request
     * @return the token as a string, or null if it is not present
     */
    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.split(" ")[1].trim();
        }
        return null;
    }

}
