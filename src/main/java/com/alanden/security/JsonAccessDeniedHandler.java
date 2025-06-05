package com.alanden.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 處理存取被拒絕的請求，返回JSON格式的錯誤訊息
@Component
public class JsonAccessDeniedHandler implements AccessDeniedHandler {
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {
		response.setStatus(403);
		response.setContentType("application/json");
		response.getWriter().write("{\"error\": \"Forbidden\"}");
	}
}