package com.alanden.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.alanden.security.JsonAuthenticationFailureHandler;
import com.alanden.security.JsonAuthenticationSuccessHandler;
import com.alanden.security.JsonUsernamePasswordAuthenticationFilter;
import com.alanden.security.JwtAuthenticationFilter;
import com.alanden.utils.JwtUtils;

/*
 * 設定Spring Security相關設定
 * PropertySource將敏感訊息保存在application-secrets.properties
 */
@Configuration
@EnableWebSecurity
@PropertySource("classpath:application-secrets.properties")
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	/**
	 * 設定HTTP安全規則
	 * @param http
	 * @param jwtFilter
	 * @param jsonFilter
	 * @return
	 * @throws Exception
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, 
			JwtAuthenticationFilter jwtFilter,
			JsonUsernamePasswordAuthenticationFilter jsonFilter,
			LogoutSuccessHandler logoutSuccessHandler)
			throws Exception {
		// 設定HTTP安全規則
		http
			// 設定CORS跨域設定
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			// 禁用CSRF防護
			.csrf(csrf -> csrf.disable())
			// 添加自定義JWT認證過濾器
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			// 添加JSON登入認證過濾器
			.addFilterBefore(jsonFilter, UsernamePasswordAuthenticationFilter.class)
			// 設定請求規則
			.authorizeHttpRequests(auth -> auth.requestMatchers("/api/login","/api/user/*").permitAll() // 允許訪問/api/login
					.anyRequest().authenticated() // 其他所有請求需要登入驗證後，才能進入。
			)
			.logout(logout -> logout
					.logoutUrl("/api/logout")
					.logoutSuccessHandler(logoutSuccessHandler)
					.invalidateHttpSession(true)
					.deleteCookies("JSESSIONID")
					.clearAuthentication(true)
			);
		return http.build();
	}

	/**
	 * 此PasswordEncoder會給AuthenticationProvider做驗證，驗證成功後會回傳Authentication。
	 * 詳細驗證邏輯可參考{@link AbstractUserDetailsAuthenticationProvider#authenticate}
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		// 設置默認的加密為bcrypt
		String idForEncode = "bcrypt";
		// 設置多種編碼算法
		Map<String, PasswordEncoder> encoders = new HashMap<>();
		encoders.put(idForEncode, new BCryptPasswordEncoder());
		encoders.put("pbkdf2",
				new Pbkdf2PasswordEncoder("secret", 16, 185000, SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256));
		encoders.put("scrypt", new SCryptPasswordEncoder(16384, 8, 1, 32, 64));

		DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder(idForEncode, encoders);
		delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());

		return delegatingPasswordEncoder;
	}

	/**
	 * 用於管理Session的物件
	 */
	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	/**
     * CORS跨域設定
     * 允許:
     * - 來源: http://localhost:8080
     * - 方法: GET/POST/PUT/DELETE/OPTIONS
     * - 所有Header
     * - 攜帶憑證(cookie等)
     */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:8080")); // ✅ 允許的前端來源
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // ✅ 允許的 HTTP 方法
		configuration.setAllowedHeaders(List.of("*")); // ✅ 允許所有 Header
		configuration.setAllowCredentials(true); // ✅ 允許 Cookie 和授權資訊

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	 /**
     * 自定義JSON登入過濾器配置
     * 
     * @param authenticationManager 認證管理器
     * @param successHandler 認證成功處理器
     * @param failureHandler 認證失敗處理器
     * 
     * 功能: 攔截/api/login請求，處理JSON格式的登入數據
     */
	@Bean
	public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter(
			AuthenticationManager authenticationManager, JsonAuthenticationSuccessHandler successHandler,
			JsonAuthenticationFailureHandler failureHandler) {
		JsonUsernamePasswordAuthenticationFilter filter = new JsonUsernamePasswordAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManager);
		filter.setAuthenticationSuccessHandler(successHandler);
		filter.setAuthenticationFailureHandler(failureHandler);
		filter.setFilterProcessesUrl("/api/login");
		return filter;
	}
	
	/**
	 * 認證管理器Bean
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
	
	@Bean
	public JwtAuthenticationFilter jwtFilter(JwtUtils provider) {
	    return new JwtAuthenticationFilter(provider);
	}
}
