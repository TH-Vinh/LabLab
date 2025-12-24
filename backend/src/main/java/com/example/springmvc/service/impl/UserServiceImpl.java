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

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserProfileResponse getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        User user = userRepository.findByUsernameWithProfile(currentUsername);

        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng: " + currentUsername);
        }
        return userMapper.toProfileDto(user);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        User user = userRepository.findByUsernameWithProfile(currentUsername);
        if (user == null) throw new RuntimeException("User not found");

        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
            user.setProfile(profile);
        }

        if (request.getFullName() != null) {
            profile.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAvatar() != null) {
            profile.setAvatar(request.getAvatar());
        }

        userRepository.save(user);
        return userMapper.toProfileDto(user);
    }
}