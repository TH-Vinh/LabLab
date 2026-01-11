package com.example.springmvc.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SecuritySettingsResponse {
    private boolean enabledTwoFa;
    private String maskedEmail;
}