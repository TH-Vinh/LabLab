package com.example.springmvc.service;

import com.example.springmvc.dto.auth.ChangePasswordRequest;
import com.example.springmvc.dto.user.UpdateProfileRequest;
import com.example.springmvc.dto.user.UserProfileResponse;

public interface UserService {
    UserProfileResponse getCurrentUser();
    UserProfileResponse updateProfile(UpdateProfileRequest request);

    void sendOtp();
    void changePassword(ChangePasswordRequest request);
}