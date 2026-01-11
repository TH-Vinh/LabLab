package com.example.springmvc.service.impl;

import com.example.springmvc.constant.RoleConst;
import com.example.springmvc.dto.LoginRequest;
import com.example.springmvc.dto.LoginResponse;
import com.example.springmvc.exception.BusinessException;
import com.example.springmvc.security.JwtUtils;
import com.example.springmvc.service.AuthService;
import lombok.RequiredArgsConstructor; // <--- 1. Import Lombok
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // <--- 2. Tự động tạo Constructor cho các field final
public class AuthServiceImpl implements AuthService {

    // 3. Chuyển tất cả @Autowired thành private final
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

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

                // 4. Dùng RoleConst thay vì chuỗi cứng
                if (RoleConst.ADMIN.equals(role) || RoleConst.TEACHER.equals(role)) {

                    // Lưu ý: Code này giả định bạn đã sửa JwtUtils theo hướng dẫn trước (nhận role)
                    String token = jwtUtils.generateToken(request.getUsername(), role);

                    response.setSuccess(true);
                    response.setMessage("Đăng nhập thành công!");
                    response.setToken(token);
                    response.setRole(role);
                } else {
                    throw new BusinessException("Tài khoản của bạn không có quyền truy cập hệ thống!");
                }
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Sai tài khoản hoặc mật khẩu!");
        }

        return response;
    }
}