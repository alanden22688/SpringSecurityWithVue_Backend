package com.alanden.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Component
public class JwtUtils {
    
	@Value("${jwt.secret-key}")  // 從設定檔案取得密鑰
    private String rawSecretKey;

    @Value("${jwt.expiration-ms}")  // 從設定檔案取得過期時間
    @Getter
    private long expirationMs;

    private SecretKey secretKey;     // 密鑰

    // 建立密鑰
    @PostConstruct 
    private void init() {
        // 將設定的字串轉換為Key物件
        secretKey = Keys.hmacShaKeyFor(
            rawSecretKey.getBytes(StandardCharsets.UTF_8)
        );
        
        // 驗證密鑰長度(HS256 需要 256 bits)
        if (secretKey.getEncoded().length < 32) {
            throw new IllegalArgumentException("密鑰需至少32位元組 (HS256)");
        }
    }

    // 生成JWT Token
    public String generateToken(String username, List<String> roles) {  // 添加角色參數
        return Jwts.builder()
                .subject(username)  // 設定主體為使用者名稱
                .claim("roles", roles)    // 將角色添加到Token中
                .expiration(new Date(System.currentTimeMillis() + expirationMs))    // 設定過期時間
                .signWith(secretKey)    // 使用密鑰簽名
                .issuedAt(new Date())   // 設定發行時間
                .compact(); // 將所有設定組合成JWT Token
    }
    
    // 驗證JWT Token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)  // 使用密鑰進行驗證
                .build()
                .parseSignedClaims(token);  // 驗證簽名
            return true;
        } catch (ExpiredJwtException e) {
            // Token 過期
            return false;
        } catch (UnsupportedJwtException | MalformedJwtException | SecurityException e) {
            // 格式錯誤或簽名無效
            return false;
        } catch (IllegalArgumentException e) {
            // 傳入的 Token 為空
            return false;
        }
    }
    
    // 從JWT Token中取得Authentication物件
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload(); // 取得JWT的Payload部分，通常包含使用者資訊和角色，不包含敏感資訊

        String username = claims.getSubject();
        // 取得角色列表
        List<?> roleList = claims.get("roles", List.class);
        // 確保角色列表是List<String>類型，並轉換為List<String>
        List<String> roles = roleList.stream()
            .filter(obj -> obj instanceof String)
            .map(obj -> (String) obj)
            .collect(Collectors.toList());

        // 將角色轉換為GrantedAuthority列表
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());

        // 返回UsernamePasswordAuthenticationToken，這是Spring Security的認證物件
        // 這裡的credentials設為null，JWT不需要密碼來驗證
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}