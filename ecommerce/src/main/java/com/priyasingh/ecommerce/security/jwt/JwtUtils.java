package com.priyasingh.ecommerce.security.jwt;

import com.priyasingh.ecommerce.security.services.UserDetailsImplementation;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.ecom.app.jwtExpirationInMs}")
    private int jwtExpirationInMS;

    @Value("${spring.ecom.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.ecom.app.jwtCookie}")
    private String jwtCookie;

//    public String getJwtFromHeader(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        logger.debug("Bearer Token: {}", bearerToken);
//        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7); // Remove Bearer Prefix
//        }
//        return null;
//    }

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);
        if(cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    public ResponseCookie generateJwtCookie(UserDetailsImplementation userPrincipal) {
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt)
                .path("/api") // this limits the cookie access to /api routes only
                .maxAge(24 * 60 * 60) // max value in seconds - this cookie is valid for one day
                .httpOnly(false) // allowing JavaScript access to this particular cookie
                .build();

        return cookie;
    }

    // Generating Token from Username
    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + jwtExpirationInMS)))
                .signWith(key())
                .compact();
    }

    // Getting Username from JWT Token
    public String getUsernameFromJWTToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    // Generating Signing Key
    public Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    // Validate JWT Token
    public boolean validateJwtToken(String token) {
        try {
            logger.debug("Validating jwt token...");
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException exception) {
            logger.error("Invalid JWT Token: {}", exception.getMessage());
        } catch (ExpiredJwtException exception) {
            logger.error("Expired JWT Token: {}", exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            logger.error("Unsupported JWT Token: {}", exception.getMessage());
        } catch (IllegalArgumentException exception) {
            logger.error("JWT Token is empty: {}", exception.getMessage());
        }
        return false;
    }
}
