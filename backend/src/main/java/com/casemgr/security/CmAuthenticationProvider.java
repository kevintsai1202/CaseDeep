package com.casemgr.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.casemgr.entity.User;
import com.casemgr.service.impl.UserServiceImpl;

public class CmAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserServiceImpl userService;

//	@Value("${spring.ldap.urls}")
//	private String ladpUrl;
//	
//	@Value("${spring.ldap.base}")
//	private String ladpBase;
//	
//	@Value("${spring.ldap.username}")
//	private String ladpUsername;
//	
//	@Value("${spring.ldap.password}")
//	private String ladpPassword;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = String.valueOf(authentication.getPrincipal());
		String password = String.valueOf(authentication.getCredentials());
		User user = (User) userService.loadUserByUsername(username);
		if (passwordEncoder.matches(password,  user.getPassword())) {
			return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
		} else {
			throw new BadCredentialsException("Error!!");	
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.equals(authentication);
	}

}
