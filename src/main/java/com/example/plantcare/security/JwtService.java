package com.example.plantcare.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Náº¯p láº¥y máº­t mÃ£ bÃ­ máº­t tá»« file application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    // Láº¥y thá»i háº¡n sá»‘ng cá»§a tháº» JWT (Máº·c Ä‘á»‹nh 1 ngÃ y)
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // NÄ‚NG Lá»°C 1: RÃšT TRÃCH LÃ•I THáºº (Ai lÃ  ngÆ°á»i mang tháº» nÃ y?)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // NÄ‚NG Lá»°C 2: MÃY IN THáºº Má»šI 
    // (ÄÆ°á»£c gá»i khi ÄÄƒng nháº­p Ä‘Ãºng Email/Password)
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // Äá»‹nh danh gá»‘c: Email
                .setIssuedAt(new Date(System.currentTimeMillis())) // Giá» ráº­p khuÃ´n
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Giá» Tháº» hÃ³a RÃ¡c
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // ÄÃ³ng dáº¥u phÃ¡p lÃ½ báº±ng Key
                .compact();
    }

    // NÄ‚NG Lá»°C 3: MÃY KIá»‚M Äá»ŠNH THáºº GIáº¢ - THáºº THáº¬T (Validate)
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // TrÃ¹ng ID ngÆ°á»i dÃ¹ng vÃ  ChÆ°a háº¿t háº¡n thÃ¬ lÃ  Äá»“ Tháº­t!
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Dao cáº¡o lÃµi chuá»—i Token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Bá»™ phÃ¢n giáº£i Key 256 bits siÃªu báº£o máº­t
    private Key getSignInKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
