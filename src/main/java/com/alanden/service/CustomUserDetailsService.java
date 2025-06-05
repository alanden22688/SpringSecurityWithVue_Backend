package com.alanden.service;
import java.util.List;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.alanden.entity.UserEntity;
import com.alanden.repository.AuthorityRepository;
import com.alanden.repository.UserRepository;

/*
 * 載入使用者和權限的資料
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
		
	private final UserRepository userRepository; // 使用者的Repository
    private final AuthorityRepository authorityRepository; // 權限的Repository
    
    public CustomUserDetailsService(UserRepository userRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 從資料庫獲得使用者訊息
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用戶不存在: " + username));

        // 從資料庫獲取權限訊息
        List<String> authorities = authorityRepository.findAuthoritiesByUsername(username);
        
        // 將使用者訊息和權限轉換成 Spring Security 的 UserDetails
        return User.builder()
                .username(user.getUsername()) // 設置使用者名稱                
                .password(user.getPassword()) // 設置密碼
                .authorities(authorities.toArray(new String[0])) // 設置使用者的權限
                .accountExpired(false) // 設置帳號是否過期
                .accountLocked(false) // 設置帳號是否被鎖定
                .credentialsExpired(false) // 設置憑證是否過期
                .disabled(!user.isEnabled()) // 設置帳號是否被禁用
                .build(); // 建立UserDetails物件
    }
    
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // 模擬從資料庫載入用戶信息
//        if ("user".equals(username)) {
//            return User.builder()
//                    .username("user")
//                    .password("$2a$10$wJcQSJhNm/z6ls8SLhW76u5ihHbNNVlcOSbnO76.34RfDsYKMxoPq") // "password" 的加密
//                    .roles("USER")
//                    .build();
//        } else if ("admin".equals(username)) {
//            return User.builder()
//                    .username("admin")
//                    .password("$2a$10$wJcQSJhNm/z6ls8SLhW76u5ihHbNNVlcOSbnO76.34RfDsYKMxoPq") // "password"
//                    .roles("ADMIN")
//                    .build();
//        }
//        throw new UsernameNotFoundException("User not found");
//    }
}