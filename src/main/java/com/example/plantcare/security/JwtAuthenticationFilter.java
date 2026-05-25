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

    // HÀM BẮT GIỮ DO-FILTER: MỌI REQUEST ĐI TỪ APP ĐỀU PHẢI CHẢY QUA ĐÂY ĐỂ KIỂM TRA HÀNH LÝ
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Vạch Hành Lý ra xem có tờ giấy khai báo Authorization không?
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Nếu Không có (Dân đen đang đứng ngó) -> Đuổi đi sang cửa khác (Chính là cổng Login/Đăng ký)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Khách Có chìa Cuốn chiếu Thẻ Bearer JWT ra
        jwt = authHeader.substring(7); // Dùng kéo cắt bỏ 7 chữ "Bearer " ở đầu câu, chỉ lấy lõi Mã code
        userEmail = jwtService.extractUsername(jwt); // Đưa lõi mã cho Máy Đọc Thẻ xem là Của Ai?

        // 3. KIỂM THỰC THẺ VÀ NHẤN NÚT MỞ CỬA CHO QUA
        // Nếu soi ra Email và Trước đây thằng này chưa được gắn biển Passed bao giờ
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Từ cái Email bị moi ra, Mò xuống Database ngầm (PostgreSQL) lôi Thẻ Căn Cước của nó lên mâm!
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Bỏ vô Máy Quẹt Thẻ đối chiếu
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Thẻ MÀU XANH, Người Thật Thẻ Thật -> Tích V Mở Cổng gác Barie cấp quyền chạy thẳng vào DB Controller
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities() // Móc cái tờ Rôle (Cán Bộ hay Khách bô) đính lên áo nó luôn trước khi cho nó đi vào sảnh
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Giáng Mộc đỏ: "THẰNG NÀY SẠCH, CHO ĐI!"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // Cú gõ búa: Cho mày lọt qua đi làm chuyện tiếp theo (Bình luận, Thả tim...)
        filterChain.doFilter(request, response);
    }
}
