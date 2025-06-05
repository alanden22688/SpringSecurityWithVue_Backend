package com.alanden.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 處理未經授權的請求，返回JSON格式的錯誤訊息
@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		response.setStatus(401);
		response.setContentType("application/json");
		response.getWriter().write("{\"error\": \"Unauthorized\"}");
	}
}