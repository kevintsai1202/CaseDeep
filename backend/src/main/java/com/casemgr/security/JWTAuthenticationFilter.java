package com.casemgr.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.casemgr.service.impl.UserServiceImpl;
import com.casemgr.utils.JwtUtils;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
	private final static String AUTH_HEADER = "authorization";
    private final static String AUTH_HEADER_TYPE = "Bearer";
	
	@Autowired
	private JwtUtils jwtUtils;
	
//	@Autowired
//    private JWTServiceImpl jwtService;
	
	@Autowired
	private UserServiceImpl userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
    		throws ServletException, IOException {
        String authHeader = request.getHeader(AUTH_HEADER);
        String jwt = "";
        
        
        if (request.getRequestURI().equals("/api/auth/login") || request.getRequestURI().equals("/")) {
			// 登入時不用檢查Token
			filterChain.doFilter(request, response);
			return;
		}
        
        if (authHeader == null || !authHeader.startsWith(AUTH_HEADER_TYPE)) {
        	filterChain.doFilter(request,response);
            return;
        }
        
		jwt = authHeader.substring(7);
//		log.info("authToken: {}" , jwt);
	
		String userName = "";
		try {
			userName = jwtUtils.extractUsername(jwt);
		}catch(ExpiredJwtException e) {
			log.error("JWT Token Expired");
			filterChain.doFilter(request, response);
			return;
		}
		
		if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails user = userService.loadUserByUsername(userName);
			
			// 添加日誌以檢查用戶的角色
			log.info("User {} has authorities: {}", user.getUsername(), user.getAuthorities());

//			boolean isTokenValid = true;
//			if (tokenValid) {
//				isTokenValid = tokenRepository.findByToken(jwt).map(t -> !t.isExpired() && !t.isRevoked())
//					.orElse(false);
//			}

//			if (jwtUtils.isTokenValid(jwt, user) && isTokenValid) {
			if (jwtUtils.isTokenValid(jwt, user)) {
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						user, null, user.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);	//將驗證後的authentication設回Context內
			}
		}
        
//        System.out.println("authHeader: "+authHeader);
//        if (authHeader.length()>7) {
//	        String authToken = authHeader.substring(7);
//	        System.out.println("authToken:"+authToken);
//	        Map payload = jwtService.parseToken(authToken);
//	        userName = payload.get("username").toString();
//	        User user = (User) userService.loadUserByUsername(userName);
//	        UsernamePasswordAuthenticationToken authentication =new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
//	        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//	        SecurityContextHolder.getContext().setAuthentication(authentication);
//	        UsernamePasswordAuthenticationToken getAuthentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//	        String testUserName = (String) getAuthentication.getPrincipal();
//	        System.out.println("testUserName:"+ testUserName);
//        }
        
        
        
        filterChain.doFilter(request, response);
    }

}
