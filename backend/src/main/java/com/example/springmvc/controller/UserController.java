package com.example.springmvc.controller;

import com.example.springmvc.dto.*;
import com.example.springmvc.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // --- QUẢN LÝ THÔNG TIN CÁ NHÂN (PROFILE) ---

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getCurrentProfile() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PatchMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }

    // --- ĐỔI MẬT KHẨU & OTP XÁC THỰC ---

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp() {
        userService.sendOtp();
        return ResponseEntity.ok(Map.of("message", "Mã OTP xác thực đã được gửi tới email của bạn!"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        // Thực hiện đổi mật khẩu kèm xác thực OTP nếu có
        userService.changePassword(request);
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công!"));
    }
}