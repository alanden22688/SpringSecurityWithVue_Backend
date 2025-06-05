//package com.alanden.security;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
///*
// * 自定義的驗證邏輯
// */
//@Component
//public class CustomAuthenticationProvider implements AuthenticationProvider {
//
//	private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);
//    private final UserDetailsService userDetailsService;
//    private final PasswordEncoder passwordEncoder;
//
//    public CustomAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
//        this.userDetailsService = userDetailsService;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//    	// 取得使用者輸入的帳號和密碼
//        String username = authentication.getName();
//        String password = authentication.getCredentials().toString();
//
//        // 根據使用者輸入的資訊，載入儲存在資料庫中的資料。
//        UserDetails user = userDetailsService.loadUserByUsername(username);       
//        
//        // 驗證密碼是否吻合
//        if (passwordEncoder.matches(password, user.getPassword())) {
//        	logger.info("驗證成功");
//        	// 吻合就回傳已驗證的Authentication物件。
//            return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
//        }
//        // 不吻合就直接拋出錯誤。
//		throw new AuthenticationException("Invalid username or password") {
//			private static final long serialVersionUID = 308677890674281821L;
//		};
//    }
//
//    @Override
//    public boolean supports(Class<?> authentication) {
//    	// 指定這個AuthenticationProvider支持的認證類型為UsernamePasswordAuthenticationToken
//        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
//    }
//    
//    /**
//     * 產生假資料的密碼。
//     * 每次產生的結果都不一樣，取一次產生結果存回資料庫。
//     */
//    public static void main(String[] args) {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String rawPassword = "user123";
//        String encodedPassword = passwordEncoder.encode(rawPassword);
//
//        System.out.println("Raw Password: " + rawPassword);
//        System.out.println("Encoded Password: " + encodedPassword);
//    }
//}
