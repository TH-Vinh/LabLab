package com.example.springmvc.service.impl;

import com.example.springmvc.dto.LoginRequest;
import com.example.springmvc.dto.LoginResponse;
import com.example.springmvc.security.JwtUtils;
import com.example.springmvc.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public LoginResponse login(LoginRequest request) {
        LoginResponse response = new LoginResponse();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                String role = userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse("");

                if (role.equals("ROLE_ADMIN") || role.equals("ROLE_TEACHER")) {
                    String token = jwtUtils.generateToken(request.getUsername());

                    response.setSuccess(true);
                    response.setMessage("Đăng nhập thành công!");
                    response.setToken(token);
                    response.setRole(role);
                } else {
                    response.setSuccess(false);
                    response.setMessage("Tài khoản của bạn không có quyền truy cập hệ thống!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setMessage("Sai tài khoản hoặc mật khẩu!");
        }

        return response;
    }
}