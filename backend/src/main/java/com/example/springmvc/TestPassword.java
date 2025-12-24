package com.example.springmvc;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "123456";
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("--------------------------------------------------");
        System.out.println("Copy dòng dưới đây và Paste vào cột password trong Database:");
        System.out.println(encodedPassword);
        System.out.println("--------------------------------------------------");
    }
}