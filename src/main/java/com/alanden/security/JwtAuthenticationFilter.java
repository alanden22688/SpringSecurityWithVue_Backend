package com.alanden.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alanden.utils.JwtUtils;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// JWT 驗證過濾器
// 繼承 OncePerRequestFilter確保每個請求只執行一次
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    // 驗證JWT Token
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
    	try {
    	    String token = resolveToken(request);
    	    if (token != null) {
    	        if (!jwtUtils.validateToken(token)) {
    	            throw new JwtException("Invalid token");
    	        }
    	        Authentication auth = jwtUtils.getAuthentication(token);
    	        SecurityContextHolder.getContext().setAuthentication(auth);
    	    }
    	} catch (JwtException e) {
    		response.setHeader("Token-Expired", "true");
    	    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token已過期");
    	    return;
    	}

        filterChain.doFilter(request, response);
    }

    // 從HTTP請求的Header中解析JWT Token
    private String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}