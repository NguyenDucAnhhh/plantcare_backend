package com.example.plantcare.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // HÃ€M Báº®T GIá»® DO-FILTER: Má»ŒI REQUEST ÄI Tá»ª APP Äá»€U PHáº¢I CHáº¢Y QUA ÄÃ‚Y Äá»‚ KIá»‚M TRA HÃ€NH LÃ
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Váº¡ch HÃ nh LÃ½ ra xem cÃ³ tá» giáº¥y khai bÃ¡o Authorization khÃ´ng?
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Náº¿u KhÃ´ng cÃ³ (DÃ¢n Ä‘en Ä‘ang Ä‘á»©ng ngÃ³) -> Äuá»•i Ä‘i sang cá»­a khÃ¡c (ChÃ­nh lÃ  cá»•ng Login/ÄÄƒng kÃ½)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. KhÃ¡ch CÃ³ chÃ¬a Cuá»‘n chiáº¿u Tháº» Bearer JWT ra
        jwt = authHeader.substring(7); // DÃ¹ng kÃ©o cáº¯t bá» 7 chá»¯ "Bearer " á»Ÿ Ä‘áº§u cÃ¢u, chá»‰ láº¥y lÃµi MÃ£ code
        userEmail = jwtService.extractUsername(jwt); // ÄÆ°a lÃµi mÃ£ cho MÃ¡y Äá»c Tháº» xem lÃ  Cá»§a Ai?

        // 3. KIá»‚M THá»°C THáºº VÃ€ NHáº¤N NÃšT Má»ž Cá»¬A CHO QUA
        // Náº¿u soi ra Email vÃ  TrÆ°á»›c Ä‘Ã¢y tháº±ng nÃ y chÆ°a Ä‘Æ°á»£c gáº¯n biá»ƒn Passed bao giá»
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Tá»« cÃ¡i Email bá»‹ moi ra, MÃ² xuá»‘ng Database ngáº§m (PostgreSQL) lÃ´i Tháº» CÄƒn CÆ°á»›c cá»§a nÃ³ lÃªn mÃ¢m!
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Bá» vÃ´ MÃ¡y Quáº¹t Tháº» Ä‘á»‘i chiáº¿u
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Tháº» MÃ€U XANH, NgÆ°á»i Tháº­t Tháº» Tháº­t -> TÃ­ch V Má»Ÿ Cá»•ng gÃ¡c Barie cáº¥p quyá»n cháº¡y tháº³ng vÃ o DB Controller
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities() // MÃ³c cÃ¡i tá» RÃ´le (CÃ¡n Bá»™ hay KhÃ¡ch bÃ´) Ä‘Ã­nh lÃªn Ã¡o nÃ³ luÃ´n trÆ°á»›c khi cho nÃ³ Ä‘i vÃ o sáº£nh
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // GiÃ¡ng Má»™c Ä‘á»: "THáº°NG NÃ€Y Sáº CH, CHO ÄI!"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // CÃº gÃµ bÃºa: Cho mÃ y lá»t qua Ä‘i lÃ m chuyá»‡n tiáº¿p theo (BÃ¬nh luáº­n, Tháº£ tim...)
        filterChain.doFilter(request, response);
    }
}
