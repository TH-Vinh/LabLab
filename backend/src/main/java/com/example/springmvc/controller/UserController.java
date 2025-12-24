package com.example.springmvc.controller;

import com.example.springmvc.dto.UpdateProfileRequest;
import com.example.springmvc.dto.UserProfileResponse;
import com.example.springmvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getCurrentProfile() {
        UserProfileResponse profile = userService.getCurrentUser();
        return ResponseEntity.ok(profile);
    }
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        UserProfileResponse updatedProfile = userService.updateProfile(request);
        return ResponseEntity.ok(updatedProfile);
    }
}