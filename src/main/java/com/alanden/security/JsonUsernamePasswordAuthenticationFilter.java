package com.alanden.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.alanden.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// 處理JSON格式的登入請求
public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		try {			
			// 從請求中讀取JSON格式的登入資料
			// 使用Jackson轉換為LoginRequest物件
			LoginRequest loginData = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

			// 取得 username 和 password
			String username = loginData.getUsername();
			String password = loginData.getPassword();

			// 建立 UsernamePasswordAuthenticationToken 物件
			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username,
					password);

			// 將包含使用者訊息的Authentication物件(authRequest)，回傳給Spring Security的認證流程。
			return getAuthenticationManager().authenticate(authRequest);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
