//package com.bookstore.security;
//
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtTokenProvider tokenProvider;
//
//    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
//        this.tokenProvider = tokenProvider;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        String requestURI = request.getRequestURI();
//        System.out.println("🎯 🎯 🎯 JWT FILTER ĐANG CHẠY - URI: " + requestURI);
//
//        try {
//            String jwt = getJwtFromRequest(request);
//            System.out.println("🔍 JWT Token: " + (jwt != null ? "CÓ - " + jwt.length() + " ký tự" : "KHÔNG"));
//
//            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
//                System.out.println("✅ Token hợp lệ");
//
//                String username = tokenProvider.getUsernameFromJwt(jwt);
//                String authString = tokenProvider.getAuthoritiesFromJwt(jwt);
//
//                System.out.println("🔍 Username: " + username);
//                System.out.println("🔍 Authorities: " + authString);
//
//                List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authString);
//
//                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                        username, null, authorities);
//                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//                System.out.println("✅ ✅ ✅ AUTHENTICATION ĐÃ ĐƯỢC SET: " + username + " - " + authorities);
//
//            } else {
//                System.out.println("❌ Không có token hợp lệ");
//            }
//        } catch (Exception ex) {
//            System.err.println("🔴 LỖI TRONG FILTER: " + ex.getMessage());
//            ex.printStackTrace();
//        }
//
//        System.out.println("🏁 KẾT THÚC FILTER CHO: " + requestURI);
//        filterChain.doFilter(request, response);
//    }
//
//    private String getJwtFromRequest(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//}
package com.bookstore.security;

// 1. Thêm 2 import
import jakarta.servlet.http.Cookie;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * Hàm doFilterInternal của bạn đã CHÍNH XÁC (đọc authorities từ token).
     * Chúng ta giữ nguyên nó.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        try {
            // 2. Hàm này SẼ ĐƯỢC SỬA ở dưới
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

                String username = tokenProvider.getUsernameFromJwt(jwt);
                String authString = tokenProvider.getAuthoritiesFromJwt(jwt);

                List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authString);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        } catch (Exception ex) {
            System.err.println("🔴 LỖI TRONG FILTER: " + ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {

        // 1. Đọc từ Cookie (cho các request tải trang)
        Cookie cookie = WebUtils.getCookie(request, "token");
        if (cookie != null && StringUtils.hasText(cookie.getValue())) {
            return cookie.getValue();
        }

        // 2. Đọc từ Header (cho các API call từ JavaScript)
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // Không tìm thấy
        return null;
    }
}