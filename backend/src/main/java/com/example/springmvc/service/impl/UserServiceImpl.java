package com.example.springmvc.service.impl;

import com.example.springmvc.dto.UpdateProfileRequest;
import com.example.springmvc.dto.UserProfileResponse;
import com.example.springmvc.entity.Profile;
import com.example.springmvc.entity.User;
import com.example.springmvc.mapper.UserMapper;
import com.example.springmvc.repository.UserRepository;
import com.example.springmvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    private final Path rootPath = Paths.get("uploads/avatars");

    @Override
    public UserProfileResponse getCurrentUser() {
        return userMapper.toProfileDto(getAuthenticatedUser());
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        User user = getAuthenticatedUser();
        Profile profile = user.getProfile();

        updatePersonalInfo(profile, request);

        if (StringUtils.hasText(request.getAvatar())) {
            updateAvatar(profile, request.getAvatar());
        }

        userRepository.save(user);
        return userMapper.toProfileDto(user);
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsernameWithProfile(username);

        if (user == null) throw new RuntimeException("User not found");
        if (user.getProfile() == null) {
            Profile newProfile = new Profile();
            newProfile.setUser(user);
            user.setProfile(newProfile);
        }
        return user;
    }

    private void updatePersonalInfo(Profile profile, UpdateProfileRequest request) {
        if (StringUtils.hasText(request.getFullName())) {
            profile.setFullName(request.getFullName().trim());
        }
        if (StringUtils.hasText(request.getPhoneNumber())) {
            profile.setPhoneNumber(request.getPhoneNumber().trim());
        }
    }

    private void updateAvatar(Profile profile, String base64Data) {
        if (!base64Data.startsWith("data:image")) return;

        try {
            if (!Files.exists(rootPath)) Files.createDirectories(rootPath);

            String[] parts = base64Data.split(",");
            String imageString = parts[1];
            String extension = parts[0].contains("jpeg") || parts[0].contains("jpg") ? ".jpg" : ".png";

            byte[] imageBytes = Base64.getDecoder().decode(imageString);
            String newFileName = UUID.randomUUID().toString() + extension;

            Files.write(rootPath.resolve(newFileName), imageBytes);

            if (StringUtils.hasText(profile.getAvatar())) {
                try { Files.deleteIfExists(rootPath.resolve(profile.getAvatar())); }
                catch (IOException ignored) {}
            }

            profile.setAvatar(newFileName);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi lưu ảnh đại diện");
        }
    }
}