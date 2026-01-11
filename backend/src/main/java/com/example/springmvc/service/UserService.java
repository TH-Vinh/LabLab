package com.example.springmvc.service;

import com.example.springmvc.dto.ChangePasswordRequest;
import com.example.springmvc.dto.UpdateProfileRequest;
import com.example.springmvc.dto.UserProfileResponse;
import com.example.springmvc.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Integer userId);
    UserResponseDTO updateUserStatus(Integer userId, Boolean isActive);
    void deleteUser(Integer userId, String currentUsername);
    
    // User profile management
    UserProfileResponse getCurrentUser();
    UserProfileResponse updateProfile(UpdateProfileRequest request);
    
    // Password and OTP
    void sendOtp();
    void changePassword(ChangePasswordRequest request);
}

