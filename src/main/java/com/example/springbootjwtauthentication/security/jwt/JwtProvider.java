package com.example.springbootjwtauthentication.security.jwt;


import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    private final String secretKey="mySecretKeyExample123IsAExampleKEYWithManyCharacterssssss";

    private Clock clock;
    private final Logger logger = LoggerFactory.getLogger(JwtProvider.class);
    private final Key SECRET_KEY = new SecretKeySpec
            (secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
    private final long EXPIRATION_TIME = 700000; //15 minutes

    public String generateToken(String email, List<String> roles) {

        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles)
                .setExpiration(Date.from(Instant.now().plusMillis(EXPIRATION_TIME)))
                .signWith(SECRET_KEY)
                .compact();
    }
    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        }catch (MalformedJwtException e){
            logger.error("The token is malformed");
        }catch (UnsupportedJwtException e){
            logger.error("Not supported");
        }catch (ExpiredJwtException e){
            logger.error("Expired token");
        }catch (IllegalArgumentException e){
            logger.error("Token is not valid");
        }catch (SignatureException e){
            logger.error("Signature is not valid");
        }
        return false;
    }

    public String getEmailFromToken(String token) {
        JwtParser parser = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build();
        return parser.parseClaimsJws(token).getBody().getSubject();
    }
/*
    public static String refreshToken(String token) {
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate);
        final Claims claims = getAllClaimsFromToken(token);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    private static  Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private static Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + EXPIRATION_TIME);
    }

    public static boolean canTokenBeRefreshed(String token) {
        final Date expiration = getExpirationDate(token);
        System.out.println("Expiration: " + expiration);
        System.out.println("Date: " + new Date());
        return expiration.after(new Date());
    }

    public static Date getExpirationDate(String token) {
        final Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        System.out.println("Expiration en getExpirationDate: " + claims.getExpiration());
        return claims.getExpiration();
    }
    public static String getTokenFromRequest(String request) {

        String bearerToken = request;
        if (StringUtils.hasText(bearerToken)) {
            return bearerToken;
        }
        return null;
    }
*/

    public Claims getFormatToken(String jwtString) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(jwtString).getBody();
    }

    //A refresh token method
    public String refreshToken(String jwtString) throws ParseException {
            System.out.println(jwtString);
            Claims claims = getFormatToken(jwtString);
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(new Date().getTime() + EXPIRATION_TIME))
                    .signWith(SECRET_KEY)
                    .compact();
    }


}
