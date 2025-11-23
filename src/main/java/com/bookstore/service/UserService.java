//package com.bookstore.service;
//
//
//import com.bookstore.dto.request.CreateUserRequest;
//import com.bookstore.dto.request.UpdateUserRequest;
//import com.bookstore.dto.response.UserResponse;
//import com.bookstore.entity.User;
//import com.bookstore.entity.UserRole;
//import com.bookstore.entity.UserStatus;
//import com.bookstore.exception.ApplicationException;
//import com.bookstore.exception.ErrorCode;
//import com.bookstore.repository.UserRepository;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class UserService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    private UserResponse toResponse(User user) {
//        UserResponse response = new UserResponse();
//        response.setId(user.getId());
//        response.setUsername(user.getUsername());
//        response.setEmail(user.getEmail());
//        response.setRole(user.getRole().name());
//        response.setStatus(user.getStatus());
//        return response;
//    }
//
//    @Transactional(readOnly = true)
//    public List<UserResponse> findAllUsers() {
//        return userRepository.findAll().stream()
//                .map(this::toResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional(readOnly = true)
//    public UserResponse findUserById(Long id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
//                        "Không tìm thấy người dùng với ID: " + id));
//        return toResponse(user);
//    }
//
//    @Transactional
//    public UserResponse createUser(CreateUserRequest request) {
//        // Kiểm tra username đã tồn tại
//        if (userRepository.existsByUsername(request.getUsername())) {
//            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA,
//                    "Tên đăng nhập đã tồn tại.");
//        }
//
//        // Kiểm tra email đã tồn tại
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA,
//                    "Email đã tồn tại.");
//        }
//
//        // Validate role
//        UserRole role;
//        try {
//            role = UserRole.valueOf(request.getRole().toUpperCase());
//        } catch (IllegalArgumentException e) {
//            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA,
//                    "Vai trò không hợp lệ. Chỉ chấp nhận: ROLE_USER, ROLE_ADMIN");
//        }
//
//        User user = new User();
//        user.setUsername(request.getUsername());
//        user.setEmail(request.getEmail());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setRole(role);
//        return toResponse(userRepository.save(user));
//    }
//
//    @Transactional
//    public UserResponse updateUser(Long id, UpdateUserRequest request) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
//                        "Không tìm thấy người dùng với ID: " + id));
//
//        // Cập nhật email nếu có
//        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
//            // Kiểm tra email trùng (trừ chính user hiện tại)
//            if (userRepository.existsByEmail(request.getEmail()) &&
//                    !user.getEmail().equals(request.getEmail())) {
//                throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA,
//                        "Email đã được sử dụng bởi người dùng khác.");
//            }
//            user.setEmail(request.getEmail());
//        }
//
//        // Cập nhật password nếu có
//        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
//            user.setPassword(passwordEncoder.encode(request.getPassword()));
//        }
//
//        // Cập nhật role nếu có
//        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
//            try {
//                UserRole role = UserRole.valueOf(request.getRole().toUpperCase());
//                user.setRole(role);
//            } catch (IllegalArgumentException e) {
//                throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA,
//                        "Vai trò không hợp lệ. Chỉ chấp nhận: ROLE_USER, ROLE_ADMIN");
//            }
//        }
//        // THÊM ĐOẠN NÀY: Cập nhật status nếu có
//        if (request.getStatus() != null) {
//            user.setStatus(request.getStatus()); // KHÔNG CẦN CONVERT NỮA
//        }
//
//
//        return toResponse(userRepository.save(user));
//    }
//
//    @Transactional
//    public void deleteUser(Long id) {
//        if (!userRepository.existsById(id)) {
//            throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
//                    "Không tìm thấy người dùng với ID: " + id);
//        }
//        userRepository.deleteById(id);
//    }
//}

package com.bookstore.service;

import com.bookstore.dto.request.CreateUserRequest;
import com.bookstore.dto.request.UpdateUserRequest;
import com.bookstore.dto.response.UserResponse;
import com.bookstore.entity.User;
import com.bookstore.entity.UserRole;
import com.bookstore.entity.UserStatus;
import com.bookstore.exception.ApplicationException;
import com.bookstore.exception.ErrorCode;
import com.bookstore.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setStatus(user.getStatus());
        return response;
    }

    // THÊM METHOD: Lấy thông tin user hiện tại
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy người dùng với username: " + username));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy người dùng với ID: " + id));
        return toResponse(user);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // Kiểm tra username đã tồn tại
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA,
                    "Tên đăng nhập đã tồn tại.");
        }

        // Kiểm tra email đã tồn tại
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA,
                    "Email đã tồn tại.");
        }

        // Validate role
        UserRole role;
        try {
            role = UserRole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA,
                    "Vai trò không hợp lệ. Chỉ chấp nhận: ROLE_USER, ROLE_ADMIN");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Không tìm thấy người dùng với ID: " + id));

        // Cập nhật email nếu có
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            // Kiểm tra email trùng (trừ chính user hiện tại)
            if (userRepository.existsByEmail(request.getEmail()) &&
                    !user.getEmail().equals(request.getEmail())) {
                throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA,
                        "Email đã được sử dụng bởi người dùng khác.");
            }
            user.setEmail(request.getEmail());
        }

        // Cập nhật password nếu có
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Cập nhật role nếu có
        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            try {
                UserRole role = UserRole.valueOf(request.getRole().toUpperCase());
                user.setRole(role);
            } catch (IllegalArgumentException e) {
                throw new ApplicationException(ErrorCode.INVALID_INPUT_DATA,
                        "Vai trò không hợp lệ. Chỉ chấp nhận: ROLE_USER, ROLE_ADMIN");
            }
        }

        // Cập nhật status nếu có
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ApplicationException(ErrorCode.RESOURCE_NOT_FOUND,
                    "Không tìm thấy người dùng với ID: " + id);
        }
        userRepository.deleteById(id);
    }

}