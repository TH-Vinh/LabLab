package com.example.springmvc.service;

import com.example.springmvc.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Integer userId);
    UserResponseDTO updateUserStatus(Integer userId, Boolean isActive);
    void deleteUser(Integer userId);
}

