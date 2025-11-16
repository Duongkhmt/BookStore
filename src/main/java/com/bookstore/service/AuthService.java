//package com.bookstore.service;
//
//import com.bookstore.dto.request.AuthRequest;
//import com.bookstore.dto.request.RegisterRequest;
//import com.bookstore.dto.response.AuthResponse;
//import com.bookstore.entity.User;
//import com.bookstore.entity.UserRole;
//import com.bookstore.exception.ApplicationException;
//import com.bookstore.exception.ErrorCode;
//import com.bookstore.repository.UserRepository;
//import com.bookstore.security.JwtTokenProvider;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AuthService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final AuthenticationManager authenticationManager;
//    private final JwtTokenProvider jwtTokenProvider;
//
//    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
//                       AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.authenticationManager = authenticationManager;
//        this.jwtTokenProvider = jwtTokenProvider;
//    }
//
//    public AuthResponse login(AuthRequest authRequest) {
//        // AuthenticationManager tự động ném BadCredentialsException nếu sai
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
//        );
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String jwt = jwtTokenProvider.generateToken(authentication);
//
//        return new AuthResponse(jwt);
//    }
//
//    public User register(RegisterRequest registerRequest) {
//        if (userRepository.existsByUsername(registerRequest.getUsername()) || userRepository.existsByEmail(registerRequest.getEmail())) {
//            throw new ApplicationException(ErrorCode.USER_ALREADY_EXISTS, "Tên người dùng hoặc email đã được sử dụng.");
//        }
//
//        User user = new User();
//        user.setUsername(registerRequest.getUsername());
//        user.setEmail(registerRequest.getEmail());
//        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
//        user.setRole(UserRole.ROLE_USER);
//
//        return userRepository.save(user);
//    }
//}
package com.bookstore.service;

import com.bookstore.dto.request.AuthRequest;
import com.bookstore.dto.request.RegisterRequest;
import com.bookstore.dto.response.AuthResponse;
import com.bookstore.entity.User;
import com.bookstore.entity.UserRole;
import com.bookstore.entity.UserStatus; // THÊM IMPORT NÀY
import com.bookstore.exception.ApplicationException;
import com.bookstore.exception.ErrorCode;
import com.bookstore.repository.UserRepository;
import com.bookstore.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public AuthResponse login(AuthRequest authRequest) {
        try {
            // 1. TÌM USER TRƯỚC ĐỂ KIỂM TRA TRẠNG THÁI
            User user = userRepository.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Tên đăng nhập hoặc mật khẩu không đúng"));

            // 2. KIỂM TRA TRẠNG THÁI TÀI KHOẢN
            if (user.getStatus() == UserStatus.LOCKED) {
                throw new ApplicationException(ErrorCode.ACCOUNT_LOCKED, "Tài khoản đã bị khóa");
            }

            if (user.getStatus() == UserStatus.INACTIVE) {
                throw new ApplicationException(ErrorCode.ACCOUNT_DISABLED, "Tài khoản không hoạt động");
            }

            // 3. XÁC THỰC USERNAME/PASSWORD
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);

            return new AuthResponse(jwt);

        } catch (BadCredentialsException e) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED, "Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }

    public User register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername()) || userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ApplicationException(ErrorCode.USER_ALREADY_EXISTS, "Tên người dùng hoặc email đã được sử dụng.");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(UserRole.ROLE_USER);
        user.setStatus(UserStatus.ACTIVE); // MẶC ĐỊNH LÀ ACTIVE KHI ĐĂNG KÝ

        return userRepository.save(user);
    }
}