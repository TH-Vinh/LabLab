package com.example.springmvc.service.impl;

import com.example.springmvc.dto.ChangePasswordRequest;
import com.example.springmvc.dto.UpdateProfileRequest;
import com.example.springmvc.dto.UserProfileResponse;
import com.example.springmvc.dto.UserResponseDTO;
import com.example.springmvc.entity.OtpType;
import com.example.springmvc.entity.Profile;
import com.example.springmvc.entity.User;
import com.example.springmvc.repository.RentTicketRepository;
import com.example.springmvc.repository.UserRepository;
import com.example.springmvc.service.OtpService;
import com.example.springmvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentTicketRepository rentTicketRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    @Override
    public UserResponseDTO updateUserStatus(Integer userId, Boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(isActive);
        user = userRepository.save(user);
        return convertToDTO(user);
    }

    @Override
    public void deleteUser(Integer userId, String currentUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Không cho phép xóa chính mình
        if (currentUsername != null && user.getUsername().equals(currentUsername)) {
            throw new RuntimeException("Bạn không thể xóa chính tài khoản của mình!");
        }
        
        // Không cho phép xóa tài khoản admin khác
        if ("ROLE_ADMIN".equals(user.getRole())) {
            throw new RuntimeException("Không thể xóa tài khoản Admin! Chỉ có thể vô hiệu hóa.");
        }
        
        // Kiểm tra xem user có rent tickets không
        long ticketCount = rentTicketRepository.findByUser_UserId(userId).size();
        if (ticketCount > 0) {
            throw new RuntimeException("Không thể xóa người dùng này vì đang có " + ticketCount + " phiếu mượn. Vui lòng vô hiệu hóa tài khoản thay vì xóa.");
        }
        
        // Xóa user (Profile sẽ được xóa tự động do cascade)
        userRepository.deleteById(userId);
    }

    private UserResponseDTO convertToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        
        if (user.getProfile() != null) {
            dto.setFullName(user.getProfile().getFullName());
            dto.setEmail(user.getProfile().getEmail());
            dto.setPhoneNumber(user.getProfile().getPhoneNumber());
            dto.setFaculty(user.getProfile().getFaculty());
            dto.setDepartment(user.getProfile().getDepartment());
        }
        
        return dto;
    }

    @Override
    public UserProfileResponse getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsernameWithProfile(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }
        return convertToProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsernameWithProfile(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }

        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
            user.setProfile(profile);
        }

        if (StringUtils.hasText(request.getFullName())) {
            profile.setFullName(request.getFullName());
        }
        if (StringUtils.hasText(request.getPhoneNumber())) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }
        if (StringUtils.hasText(request.getEmail())) {
            profile.setEmail(request.getEmail());
        }
        if (StringUtils.hasText(request.getAvatar())) {
            profile.setAvatar(request.getAvatar());
        }

        user = userRepository.save(user);
        return convertToProfileResponse(user);
    }

    @Override
    @Transactional
    public void sendOtp() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsernameWithProfile(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }
        if (user.getProfile() == null || !StringUtils.hasText(user.getProfile().getEmail())) {
            throw new RuntimeException("Bạn chưa có email trong hồ sơ. Vui lòng cập nhật email trước!");
        }
        otpService.generateAndSendOtp(user, OtpType.CHANGE_PASSWORD);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác!");
        }

        // Kiểm tra mật khẩu mới và xác nhận
        if (!StringUtils.hasText(request.getNewPassword()) || request.getNewPassword().length() < 6) {
            throw new RuntimeException("Mật khẩu mới phải có tối thiểu 6 ký tự!");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu mới và xác nhận không khớp!");
        }

        // Nếu có OTP code, xác thực OTP
        if (StringUtils.hasText(request.getOtpCode())) {
            user = userRepository.findByUsernameWithProfile(username); // Load lại với profile
            otpService.verifyOtp(user, request.getOtpCode(), OtpType.CHANGE_PASSWORD);
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private UserProfileResponse convertToProfileResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());

        if (user.getProfile() != null) {
            response.setFullName(user.getProfile().getFullName());
            response.setEmail(user.getProfile().getEmail());
            response.setPhoneNumber(user.getProfile().getPhoneNumber());
            response.setDepartment(user.getProfile().getDepartment());
            response.setFaculty(user.getProfile().getFaculty());
            response.setAvatar(user.getProfile().getAvatar());
        }

        return response;
    }
}

