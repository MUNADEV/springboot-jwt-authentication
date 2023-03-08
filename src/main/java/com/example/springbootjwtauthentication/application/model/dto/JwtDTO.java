package com.example.springbootjwtauthentication.application.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
/**
 * DTO Token
 */
public class JwtDTO {
    private final String prefix = "Bearer ";
    private String token;

    public String  allToken(){
        return this.prefix+this.token;
    }

}