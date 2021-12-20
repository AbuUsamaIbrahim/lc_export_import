package com.pacific.restapi.jwtconfig;

import com.pacific.restapi.dto.UserPrincipal;
import com.pacific.restapi.util.DateUtils;
import io.jsonwebtoken.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LogManager.getLogger(JwtTokenProvider.class.getName());

    @Value("${jwt.token.secret}")
    private String jwtSecret;

    @Value("${jwt.expire.minutes}")
    private int jwtExpirationInMs;
    @Value("${jwt.expire.hours}")
    private Long expireHours;

    public String generateToken(Authentication authentication) {

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        /*Calendar date = Calendar.getInstance();
        long timeInMillis= date.getTimeInMillis();
        Date expiryDate=new Date(timeInMillis + (10 * jwtExpirationInMs));*/

        return Jwts.builder().setId(UUID.randomUUID().toString())
                .claim("email", userPrincipal.getEmail())
                .claim("userName", userPrincipal.getUsername())
                .setSubject(String.valueOf(userPrincipal.getId()))
                .setIssuedAt(now).setExpiration(DateUtils.getExpirationTime(expireHours))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.valueOf(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }
}