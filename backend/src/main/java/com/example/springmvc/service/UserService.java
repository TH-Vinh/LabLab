package com.example.springmvc.service;

import com.example.springmvc.dto.*;

public interface UserService {
    UserProfileResponse getCurrentUser();
    UserProfileResponse updateProfile(UpdateProfileRequest request);

    void sendOtp();
    void changePassword(ChangePasswordRequest request);
}