package com.example.springbootjwtauthentication.security.jwt;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
public class JwtProvider {

    private static final Key SECRET_KEY = new SecretKeySpec
            ("mySecretKey_Example".
            getBytes(), SignatureAlgorithm.HS512.getJcaName());
    private static final long EXPIRATION_TIME = 900_000; //15 minutes

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(Date.from(Instant.now().plusMillis(EXPIRATION_TIME)))
                .signWith(SECRET_KEY)
                .compact();
    }
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getUsernameFromToken(String token) {
        JwtParser parser = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build();
        return parser.parseClaimsJws(token).getBody().getSubject();
    }

}
