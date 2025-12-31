package com.casemgr.service.impl;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.NoSuchAlgorithmException;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import com.casemgr.entity.User;
import com.casemgr.repository.UserRepository;
import com.casemgr.request.AuthRequest;
import com.casemgr.request.RegisterRequest;
import com.casemgr.response.TokenResponse;
import com.casemgr.utils.JwtUtils;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

//@ConfigurationProperties(prefix = "jwt")
@Data
@Service
public class AuthServiceImpl {
//	private String secretKey;
//	private int lifeTime;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private HttpServletRequest httpRequest;
	
	@Autowired
	private UserServiceImpl userService;
	
	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	PasswordEncoder passwordEncoder;
//	private YmlData ymlData;

	@Resource
	private UserRepository userRepository;

	public TokenResponse authenticate(AuthRequest request) throws AuthenticationException, UserPrincipalNotFoundException, NoSuchAlgorithmException {
		User user = (User) userService.loadUserByUsername(request.getUsername());
//		String encodePwd = passwordEncoder.encode(request.getPassword());
		
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new AuthenticationException("Password not match!");
		}
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				user, null, user.getAuthorities());
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String token = jwtUtils.createToken(authentication);
		
		return new TokenResponse("Authorization successful", user.getUId(), token);
	}
	
	public TokenResponse authenticate(RegisterRequest request) throws AuthenticationException, UserPrincipalNotFoundException, NoSuchAlgorithmException {
		User user = (User) userService.loadUserByUsername(request.getUsername());
//		String encodePwd = passwordEncoder.encode(request.getPassword());
		
		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new AuthenticationException("Password not match!");
		}
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				user, null, user.getAuthorities());
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		String token = jwtUtils.createToken(authentication);
		
		return new TokenResponse("Authorization successful", user.getUId(), token);
	}
	
//	public String generateToken(AuthRequest request) throws UserPrincipalNotFoundException, NoSuchAlgorithmException, AuthenticationException {
////		String authenticateMethod = ymlData.getAuthenticateMethod();
////		LdapUser ldapUser = null;
////		String requestBCryPwd = "";
////		if ("ldap".equals(authenticateMethod)) {
////			ldapUser = ldapService.loginByLdap(request);
////			System.out.println(ldapUser);
////		}
//		User user = null;
//		user = (User) userService.loadUserByUsername(request.getUsername());
//		System.out.println("user: " + user);
//		System.out.println("account user:" + user.getUsername());
//		System.out.println("account pwd:" + user.getPassword());
////		requestBCryPwd = passwordEncoder.encode( request.getPassword());
////		requestBCryPwd = DigestUtils.md5DigestAsHex(request.getPassword().getBytes()).toUpperCase();
//		System.out.println("login pwd:" + request.getPassword());
//		
//		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//			throw new AuthenticationException("Password not match!");
//		}
////			if ("ladp".equalsIgnoreCase(authenticateMethod) && (user.getEmail() == null)) {
////				System.out.println("Sync email");
////				user.setEmail(ldapUser.getMail());
////				user = zaccountRepository.save(zuser);
////			}
//
//		Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(),
//				request.getPassword());
//		authentication = authenticationManager.authenticate(authentication);
////        ZAccount user = (ZAccount) userService.loadUserByUsername(username);
//		return jwtUtils.createToken(user.getUsername());
//		
////		Calendar calendar = Calendar.getInstance();
////		calendar.add(Calendar.SECOND, lifeTime);
////
////		Claims claims = Jwts.claims();
////		claims.put("username", user.getUsername());
//////		claims.put("userid", user.getUsername());
////		claims.put("email", user.getEmail());
////		claims.setExpiration(calendar.getTime());
////		claims.setIssuer("Case Management System");
////		System.out.println(secretKey);
////		Key key = Keys.hmacShaKeyFor(this.secretKey.getBytes());
//		
////		return Jwts.builder().setClaims(claims).signWith(key).compact();
//	}

//	public Map<String, Object> parseToken(String token) {
//		if (token == null)
//			throw new NullPointerException("token is null!");
//
//		Key key = Keys.hmacShaKeyFor(this.secretKey.getBytes());
//
//		JwtParser parser = Jwts.parserBuilder().setSigningKey(key).build();
//
//		Claims claims = parser.parseClaimsJws(token).getBody();
//
//		return claims.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//	}
//
//	public Boolean isExpired(String token) {
//		Key key = Keys.hmacShaKeyFor(this.secretKey.getBytes());
//
//		JwtParser parser = Jwts.parserBuilder().setSigningKey(key).build();
//
//		Claims claims = parser.parseClaimsJws(token).getBody();
//		Date expiration = claims.getExpiration();
//
//		return new Date(System.currentTimeMillis()).after(expiration);
//	}

}
