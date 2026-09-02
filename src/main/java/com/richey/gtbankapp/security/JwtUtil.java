package com.richey.gtbankapp.security;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private String secret = "23113811013722";
    private Long expirationMs = 3600000L;


    // Get Sigin Key
    private SecretKey getSignInKey(){
        byte[] KeyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(KeyBytes);
    }

    //Generate Token
    public String generateToken(UserDetails userDetails){
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignInKey())
                .compact();
    }

    //Extract Claims from Token

    private Claims extractClaims(String token){
                return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
    }

    // Extract Username
    public String extractUsername(String token){
            return extractClaims(token).getSubject();
    }

    // Check if Token is expired
    public boolean isTokenexpired(String token){
        return extractClaims(token).getExpiration().before(new Date());
    }

    //Check if token is valid
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenexpired(token);
    }

}

