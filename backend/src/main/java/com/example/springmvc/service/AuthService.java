package com.example.springmvc.service;

import com.example.springmvc.dto.LoginRequest;
import com.example.springmvc.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}