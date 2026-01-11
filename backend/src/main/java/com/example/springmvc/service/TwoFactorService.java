package com.example.springmvc.service;

import com.example.springmvc.dto.Enable2FaRequest;
import com.example.springmvc.dto.SecuritySettingsResponse;
import com.example.springmvc.dto.VerifyOtpRequest;

public interface TwoFactorService {
    SecuritySettingsResponse getSecuritySettings();

    void sendSetupOtp(Enable2FaRequest request);
    void verifyAndEnable2Fa(VerifyOtpRequest request);

    void requestChangeEmail(String newEmail);
    void confirmChangeEmail(String newEmail, String otp);

    void disable2Fa();
}