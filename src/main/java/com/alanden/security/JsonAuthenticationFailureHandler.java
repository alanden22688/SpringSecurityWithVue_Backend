package com.alanden.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 處理登入失敗後的JSON格式回應
@Component
public class JsonAuthenticationFailureHandler implements AuthenticationFailureHandler {
	// 當認證失敗時，返回JSON格式的錯誤訊息
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException {
		response.setStatus(401);
		response.setContentType("application/json");
		response.getWriter().write("{\"success\": false, \"error\": \"Invalid credentials\"}");
	}
}