package com.example.springmvc.service;

import com.example.springmvc.dto.UserResponseDTO;
import com.example.springmvc.dto.auth.ChangePasswordRequest;
import com.example.springmvc.dto.user.UpdateProfileRequest;
import com.example.springmvc.dto.user.UserProfileResponse;

import java.util.List;

public interface UserService {
    UserProfileResponse getCurrentUser();
    UserProfileResponse updateProfile(UpdateProfileRequest request);

    void sendOtp();
    void changePassword(ChangePasswordRequest request);
    
    // Admin methods
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Integer userId);
    UserResponseDTO updateUserStatus(Integer userId, Boolean isActive);
    void deleteUser(Integer userId, String currentUsername);
}