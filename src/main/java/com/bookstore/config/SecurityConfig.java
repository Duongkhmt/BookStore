package com.bookstore.config;

import com.bookstore.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus; // Cần import cái này
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // Yêu cầu Spring Security bỏ qua hoàn toàn các đường dẫn này
        return (web) -> web.ignoring().requestMatchers(
                "/", "/login", "/register",
                "/css/**",
                "/js/**",
                "/images/**",
                "/webjars/**",
                "/*.html",
                "/favicon.ico", // <-- SỬA LỖI 1: Thêm favicon
                "/error",
                "/public/**"
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ADMIN MODULES dùng @PreAuthorize
                        .requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers("/api/inventory/**").permitAll()
                        .requestMatchers("/api/receipts/**").permitAll()
                        .requestMatchers("/api/suppliers/**").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()

                        // CATEGORY
                        .requestMatchers("/api/categories", "/api/categories/**")
                        .hasAnyRole("ADMIN", "USER")

                        // BOOKS
                        .requestMatchers("/api/books", "/api/books/**")
                        .hasAnyRole("ADMIN", "USER")

                        // ORDERS
                        .requestMatchers("/api/orders", "/api/orders/**")
                        .hasAnyRole("ADMIN", "USER")

                        // PAYMENTS
                        .requestMatchers("/api/payments/**").hasRole("USER")

                        // UI pages
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/cart/**", "/orders/**").hasRole("USER")

                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception
                        // Xử lý khi CHƯA ĐĂNG NHẬP (401 Unauthorized)
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\": \"Unauthorized\"}");
                            } else {
                                response.sendRedirect("/");
                            }
                        })
                        // <-- SỬA LỖI 3: Xử lý khi ĐÃ ĐĂNG NHẬP nhưng KHÔNG CÓ QUYỀN (403 Forbidden)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                // Nếu là API, trả về JSON 403
                                response.setStatus(HttpStatus.FORBIDDEN.value());
                                response.setContentType("application/json");
                                response.getWriter().write("{\"error\": \"Access Denied\"}");
                            } else {
                                // Nếu là trang web, redirect về trang chủ
                                response.sendRedirect("/");
                            }
                        })
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}