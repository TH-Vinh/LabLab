package com.example.springmvc.service.impl;

import com.example.springmvc.dto.UserResponseDTO;
import com.example.springmvc.dto.auth.ChangePasswordRequest;
import com.example.springmvc.dto.user.UpdateProfileRequest;
import com.example.springmvc.dto.user.UserProfileResponse;
import com.example.springmvc.entity.*;
import com.example.springmvc.exception.BusinessException;
import com.example.springmvc.mapper.UserMapper;
import com.example.springmvc.repository.UserRepository;
import com.example.springmvc.repository.UserSecuritySettingsRepository;
import com.example.springmvc.service.OtpService;
import com.example.springmvc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSecuritySettingsRepository securityRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    @Value("${file.upload-dir}")
    private String uploadDirConfig;

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsernameWithProfile(auth.getName());

        if (user == null) throw new BusinessException("Không tìm thấy người dùng!");
        if (user.getProfile() == null) {
            Profile p = new Profile();
            p.setUser(user);
            user.setProfile(p);
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng với ID: " + userId));
        return convertToUserResponseDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserStatus(Integer userId, Boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng với ID: " + userId));
        
        user.setIsActive(isActive);
        userRepository.save(user);
        return convertToUserResponseDTO(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId, String currentUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng với ID: " + userId));
        
        // Không cho phép xóa admin
        if ("ROLE_ADMIN".equals(user.getRole())) {
            throw new BusinessException("Không thể xóa tài khoản Admin!");
        }
        
        // Không cho phép xóa chính mình
        if (user.getUsername().equals(currentUsername)) {
            throw new BusinessException("Không thể xóa chính tài khoản của bạn!");
        }
        
        userRepository.delete(user);
    }
    
    private UserResponseDTO convertToUserResponseDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        
        // Lấy thông tin từ profile nếu có
        if (user.getProfile() != null) {
            Profile profile = user.getProfile();
            dto.setFullName(profile.getFullName());
            dto.setEmail(profile.getEmail());
            dto.setPhoneNumber(profile.getPhoneNumber());
            dto.setFaculty(profile.getFaculty());
            dto.setDepartment(profile.getDepartment());
        }
        
        return dto;
    }

    @Override
    public UserProfileResponse getCurrentUser() {
        return userMapper.toProfileDto(getAuthenticatedUser());
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        User user = getAuthenticatedUser();
        Profile profile = user.getProfile();

        if (StringUtils.hasText(request.getFullName())) profile.setFullName(request.getFullName().trim());
        if (StringUtils.hasText(request.getPhoneNumber())) profile.setPhoneNumber(request.getPhoneNumber().trim());
        if (StringUtils.hasText(request.getAvatar())) updateAvatar(profile, request.getAvatar());

        userRepository.save(user);
        return userMapper.toProfileDto(user);
    }

    @Override
    @Transactional
    public void sendOtp() {
        User user = getAuthenticatedUser();
        UserSecuritySettings settings = securityRepository.findByUser_UserId(user.getUserId())
                .orElse(null);

        if (settings == null || !Boolean.TRUE.equals(settings.getIsTwoFaEnabled())) {
            throw new BusinessException("Tài khoản chưa bật bảo mật 2 lớp, không cần gửi mã OTP!");
        }

        otpService.generateAndSendOtp(user, settings.getSecurityEmail(), OtpType.CHANGE_PASSWORD);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = getAuthenticatedUser();

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("Mật khẩu cũ không chính xác!");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Mật khẩu xác nhận không khớp!");
        }

        UserSecuritySettings settings = securityRepository.findByUser_UserId(user.getUserId())
                .orElse(null);

        boolean is2FaEnabled = settings != null && Boolean.TRUE.equals(settings.getIsTwoFaEnabled());

        if (is2FaEnabled) {
            if (!StringUtils.hasText(request.getOtpCode())) {
                throw new BusinessException("Tài khoản đang bật bảo mật 2 lớp. Vui lòng nhập mã OTP!");
            }

            otpService.verifyOtp(user, request.getOtpCode(), settings.getSecurityEmail(), OtpType.CHANGE_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private void updateAvatar(Profile profile, String base64Data) {
        if (!base64Data.startsWith("data:image")) return;
        try {
            Path rootLocation = Paths.get(uploadDirConfig).resolve("avatars");
            if (!Files.exists(rootLocation)) Files.createDirectories(rootLocation);

            String[] parts = base64Data.split(",");
            String extension = parts[0].contains("jpeg") ||
                    parts[0].contains("jpg") ? ".jpg" : ".png";
            byte[] imageBytes = Base64.getDecoder().decode(parts[1]);
            String newFileName = UUID.randomUUID().toString() + extension;

            Files.write(rootLocation.resolve(newFileName), imageBytes);
            profile.setAvatar(newFileName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("Lỗi hệ thống khi lưu ảnh đại diện.");
        }
    }
}