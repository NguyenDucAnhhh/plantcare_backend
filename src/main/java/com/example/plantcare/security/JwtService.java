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

    // Nắp lấy mật mã bí mật từ file application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    // Lấy thời hạn sống của thẻ JWT (Mặc định 1 ngày)
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // NĂNG LỰC 1: RÚT TRÍCH LÕI THẺ (Ai là người mang thẻ này?)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // NĂNG LỰC 2: MÁY IN THẺ MỚI 
    // (Được gọi khi Đăng nhập đúng Email/Password)
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // Định danh gốc: Email
                .setIssuedAt(new Date(System.currentTimeMillis())) // Giờ rập khuôn
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Giờ Thẻ hóa Rác
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Đóng dấu pháp lý bằng Key
                .compact();
    }

    // NĂNG LỰC 3: MÁY KIỂM ĐỊNH THẺ GIẢ - THẺ THẬT (Validate)
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Trùng ID người dùng và Chưa hết hạn thì là Đồ Thật!
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Dao cạo lõi chuỗi Token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Bộ phân giải Key 256 bits siêu bảo mật
    private Key getSignInKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
