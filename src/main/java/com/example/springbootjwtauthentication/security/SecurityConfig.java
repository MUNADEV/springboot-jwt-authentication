package com.example.springbootjwtauthentication.security;

import com.example.springbootjwtauthentication.exceptions.jwt.CustomAccessDenied;
import com.example.springbootjwtauthentication.exceptions.jwt.CustomAuthenticationError;
import com.example.springbootjwtauthentication.security.jwt.RequestFilterJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig{

    @Autowired
    private UserDetail userDetail;
    @Autowired
    private CustomAuthenticationError customAuthenticationError;
    @Autowired
    private CustomAccessDenied customAccessDenied;
    @Bean
    RequestFilterJWT requestFilterJWT() {
        return new RequestFilterJWT();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeHttpRequests(authConfig -> {
                    authConfig.requestMatchers("/auth/**").permitAll();
                    authConfig.anyRequest().authenticated();
                }).exceptionHandling().authenticationEntryPoint(customAuthenticationError).accessDeniedHandler(customAccessDenied).and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.userDetailsService(userDetail);
        http.addFilterBefore(requestFilterJWT(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
