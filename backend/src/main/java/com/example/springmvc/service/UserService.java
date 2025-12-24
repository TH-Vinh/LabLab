package com.example.springmvc.service;

import com.example.springmvc.dto.UpdateProfileRequest;
import com.example.springmvc.dto.UserProfileResponse;

public interface UserService {
    UserProfileResponse getCurrentUser();

    UserProfileResponse updateProfile(UpdateProfileRequest request);
}