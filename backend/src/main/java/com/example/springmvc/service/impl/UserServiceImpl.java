package com.example.springmvc.service.impl;

import com.example.springmvc.dto.UserResponseDTO;
import com.example.springmvc.entity.User;
import com.example.springmvc.repository.UserRepository;
import com.example.springmvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

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
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
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
}

