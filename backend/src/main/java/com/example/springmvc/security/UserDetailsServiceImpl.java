package com.example.springmvc.security;

import com.example.springmvc.constant.RoleConst; // <--- Import Constant
import com.example.springmvc.entity.User;
import com.example.springmvc.exception.BusinessException;
import com.example.springmvc.repository.UserRepository;
import lombok.RequiredArgsConstructor; // <--- Dùng Lombok cho sạch code
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng: " + username);
        }

        String roleFromDB = user.getRole();

        String finalRole = roleFromDB.startsWith("ROLE_") ? roleFromDB : "ROLE_" + roleFromDB;

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(finalRole)))
                .accountExpired(false)
                .accountLocked(!Boolean.TRUE.equals(user.getIsActive()))
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}