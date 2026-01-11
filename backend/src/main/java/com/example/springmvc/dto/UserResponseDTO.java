package com.example.springmvc.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Integer userId;
    private String username;
    private String role;
    private Boolean isActive;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // Profile info
    private String fullName;
    private String email;
    private String phoneNumber;
    private String faculty;
    private String department;
}

