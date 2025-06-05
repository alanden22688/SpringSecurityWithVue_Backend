package com.alanden.security;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.alanden.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 處理登入成功後的JSON格式回應
@Component
public class JsonAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	@Autowired
    private JwtUtils jwtUtils;
	
	@Autowired
    private ObjectMapper objectMapper;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		String username = authentication.getName();
		// 從 Authentication 中提取角色名稱列表
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
		// 使用 JwtUtils產生JWT Token
		String token = jwtUtils.generateToken(username, roles);
		long expirationMs = jwtUtils.getExpirationMs();
		
		// 準備回應的JSON格式資料
		Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("success", true);
        responseBody.put("token", token);
        responseBody.put("roles", roles);
        responseBody.put("expires_in", expirationMs / 1000);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), responseBody);
	}
}