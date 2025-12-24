package com.example.springmvc;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import javax.crypto.SecretKey;

public class KeyGenerator {
    public static void main(String[] args) {
        // 1. Sinh một khóa ngẫu nhiên đảm bảo đủ độ mạnh cho thuật toán HS256 (256-bit)
        SecretKey key = Jwts.SIG.HS256.key().build();

        // 2. Chuyển khóa đó thành chuỗi Base64 để có thể lưu vào file properties
        String secretString = Encoders.BASE64.encode(key.getEncoded());

        System.out.println("--- COPY CHUỖI DƯỚI ĐÂY ---");
        System.out.println(secretString);
        System.out.println("---------------------------");
    }
}