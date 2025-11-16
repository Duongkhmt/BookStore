//package com.bookstore.config;
//
//import com.bookstore.entity.User;
//import com.bookstore.entity.UserRole;
//import com.bookstore.repository.UserRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DataInitializer implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Override
//    public void run(String... args) {
//        // Tạo admin user nếu chưa tồn tại
//        if (!userRepository.existsByUsername("admin")) {
//            User admin = new User();
//            admin.setUsername("admin");
//            admin.setEmail("admin@bookstore.com");
//            admin.setPassword(passwordEncoder.encode("admin123")); // Mật khẩu mặc định
//            admin.setRole(UserRole.ROLE_ADMIN);
//            userRepository.save(admin);
//            System.out.println("✅ Đã tạo tài khoản admin: username=admin, password=admin123");
//        }
//
//        // Tạo user test nếu chưa tồn tại
//        if (!userRepository.existsByUsername("user")) {
//            User user = new User();
//            user.setUsername("user");
//            user.setEmail("user@bookstore.com");
//            user.setPassword(passwordEncoder.encode("user123"));
//            user.setRole(UserRole.ROLE_USER);
//            userRepository.save(user);
//            System.out.println("✅ Đã tạo tài khoản user: username=user, password=user123");
//        }
//    }
//}
