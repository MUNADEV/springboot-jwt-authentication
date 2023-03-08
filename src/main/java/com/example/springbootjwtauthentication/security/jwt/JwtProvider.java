package com.example.springbootjwtauthentication.security.jwt;

import com.example.springbootjwtauthentication.application.model.dto.JwtDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtProvider {
    private final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

     /**
     * The secret key used to sign the JWT token.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * The expiration time in milliseconds for the JWT token.
     */
    @Value("${jwt.expiration}")
    private int expiration;

    /**
     * Generates a JWT token for the given email and roles.
     *
     * @param email the email of the user for whom the token is being generated.
     * @param roles the roles of the user for whom the token is being generated.
     * @return a string representing the generated JWT token.
     */
    public String generateToken(String email, List<String> roles) {

        return Jwts.builder()
                .setSubject(email)
                .claim("roles", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(Instant.now().plusMillis(expiration)))
                .signWith(getSignInKey(),SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the given JWT token.
     *
     * @param token the JWT token to validate.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token);
            return true;
        }catch (MalformedJwtException e){
            logger.error("The token is malformed");
        }catch (UnsupportedJwtException e){
            logger.error("Not supported");
        }catch (ExpiredJwtException e){
            logger.error("Expired token session");
            return false;
        }catch (IllegalArgumentException e){
            logger.error("Token is not valid");
        }catch (SignatureException e){
            logger.error("Signature is not valid");
        }
        return false;
    }

    /**
     * Extracts the email from the given JWT token.
     *
     * @param token the JWT token from which to extract the email.
     * @return the email extracted from the token.
     */
    public String getEmailFromToken(String token) {
        JwtParser parser = Jwts.parserBuilder().setSigningKey(getSignInKey()).build();
        return parser.parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Extracts all claims from the given JWT token.
     *
     * @param token the JWT token from which to extract the claims.
     * @return the claims extracted from the token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generates a new JWT token with refreshed expiration time.
     *
     * @param jwtDto the JWT token from which to extract the claims.
     * @return a string representing the new JWT token with refreshed expiration time if actual expiration time is correct.
     */
    public String refreshToken(JwtDTO jwtDto) {
        Claims claims;

        try{
            claims = extractAllClaims(jwtDto.getToken());
        }catch (ExpiredJwtException e) {
            logger.error("Expired token");
            return null;
        }
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationTime)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * This method generates a secret key to sign and verify JWT tokens.
     * The key is generated by decoding the secret key provided in the application properties.
     * @return A Key object containing the secret key.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getEmailFromToken(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

}
