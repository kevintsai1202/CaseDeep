package com.casemgr.controller;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateExpiredException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.casemgr.converter.UserConverter;
import com.casemgr.entity.User;
import com.casemgr.exception.BusinessException;
import com.casemgr.request.AuthRequest;
import com.casemgr.request.ForgotPasswordRequest;
import com.casemgr.request.ReSendVerifyCodeRequest;
import com.casemgr.request.RegisterRequest;
import com.casemgr.request.ResetPasswordRequest;
import com.casemgr.request.VerifyEmailRequest;
import com.casemgr.response.MessageResponse;
import com.casemgr.response.TokenResponse;
import com.casemgr.response.UserResponse;
import com.casemgr.service.impl.AuthServiceImpl;
import com.casemgr.service.impl.UserServiceImpl;
import com.casemgr.utils.JwtUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Authentication API", description = "APIs for user authentication, registration, password management, and token operations")
@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
    private AuthServiceImpl authService;
	
	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private UserServiceImpl userService;
//
	@Operation(
		summary = "User Login",
		description = "Authenticate user credentials and return JWT token with user information. " +
					  "Requires valid username/email and password. Returns token response with user ID and JWT token for subsequent API calls."
	)
	@PostMapping("/login")
	   public ResponseEntity<TokenResponse> login(
	   	@Parameter(description = "Login credentials containing username/email and password", required = true)
	   	@Valid @RequestBody AuthRequest request) throws UserPrincipalNotFoundException, NoSuchAlgorithmException {
		TokenResponse tokenResponse;
//		try {
			try {
				tokenResponse = authService.authenticate(request);
				return ResponseEntity.ok(tokenResponse);
			
			} catch (AuthenticationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenResponse("Authorization failed"));
			} catch (UserPrincipalNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenResponse("User not fount"));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenResponse("Unknow error"));
			}
//		} catch (AuthenticationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenResponse(""));
//		} catch (UserPrincipalNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message","Account not exist"));
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message","Can't generate token"));
//		}
        
//        Map<String, Object> payLoad = jwtService.parseToken(token);
//        Map<String, Object> response = Map.of(
//                "token", token,
//                "userId", user.getUId()
//            );
//        response.putAll(payLoad);
        
    }
	
	@Operation(
		summary = "Forgot Password",
		description = "Send password reset email to user. Validates that the provided username matches the email address. " +
					  "If validation passes, generates a reset code and sends it to the user's email address."
	)
	@PostMapping("/forgetpassword")
	public ResponseEntity<MessageResponse> forgetPassword(
		@Parameter(description = "Forgot password request containing username and email address", required = true)
		@Valid @RequestBody ForgotPasswordRequest request){
		
		User user = (User) userService.loadUserByUsername(request.getUsername());
		MessageResponse response = null;
		if (user.getEmail().equals(request.getEmail())) {
			userService.generateResetCode(user);
			response = new MessageResponse("A reset email has been sent to your inbox.");
		}else {
			response = new MessageResponse("Username and email not match.");
		}
		return ResponseEntity.ok(response);
	}
	
	@Operation(
		summary = "User Registration",
		description = "Register a new user account with email, username, and password. " +
					  "Creates a new user account and automatically logs them in, returning a JWT token. " +
					  "Email verification may be required depending on system configuration."
	)
	@PostMapping("/register")
	   public ResponseEntity<TokenResponse> register(
	   	@Parameter(description = "Registration request containing email, username, and password", required = true)
	   	@Valid @RequestBody RegisterRequest request) throws UserPrincipalNotFoundException, NoSuchAlgorithmException {
//		User user = UserConverter.INSTANCT.requestToEntity(request);
//		user.setType("U");
		TokenResponse tokenResponse;
		User newUser = userService.createUser(request);
		try {
			tokenResponse = authService.authenticate(request);
			return ResponseEntity.ok(tokenResponse);
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenResponse("Authorization failed"));
		} catch (UserPrincipalNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenResponse("User not fount"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenResponse("Unknow error"));
		}	
//        return ResponseEntity.ok(UserConverter.INSTANCT.entityToResponse(newUser));
    }
	
	@Operation(
		summary = "Parse JWT Token",
		description = "Parse and extract payload information from a JWT token. " +
					  "Returns decoded token information including user details, roles, and expiration time. " +
					  "Useful for client-side token validation and user information extraction."
	)
	@GetMapping("/parse")
	   public ResponseEntity<Map<String, Object>> parseToken(
	   	@Parameter(description = "Request body containing the JWT token to parse", required = true)
	   	@RequestBody Map<String, String> request) {
        String token = request.get("token");
        Map<String, Object> response = jwtUtils.parseToken(token);

        return ResponseEntity.ok(response);
    }
	
	@Operation(
		summary = "Reset Password",
		description = "Reset user password using a valid reset code. " +
					  "The reset code must be obtained through the forgot password process. " +
					  "Once validated, the user's password will be updated to the new password provided."
	)
	@PostMapping("/resetpassword")
	   public ResponseEntity<MessageResponse> resetPassword(
	   	@Parameter(description = "Password reset code received via email", required = true)
	   	@RequestParam("resetcode") String resetCode,
	   	@Parameter(description = "Request containing the new password", required = true)
	   	@RequestBody ResetPasswordRequest request) {
        try {
			userService.resetPassword(resetCode, request.getNewPassword());
			return ResponseEntity.ok(new MessageResponse("Password has been reset successfully."));
		} catch (BusinessException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
		}
    }
    
	@Operation(
		summary = "Check Token Expiration",
		description = "Validate whether the provided JWT token has expired. " +
					  "Extracts token from Authorization header and checks its expiration status. " +
					  "Returns success message if token is valid, throws exception if expired."
	)
	   @GetMapping("/isexpired")
	   public ResponseEntity<Map<String, String>> isExpired(
	   	@Parameter(description = "HTTP headers containing Authorization header with Bearer token", required = true)
	   	@RequestHeader Map<String, String> header) throws CertificateExpiredException {
        String authorization = header.get("authorization");
        String token = authorization.split(" ")[1];
        System.out.println("token: "+ token);
        Map<String, String> response = null;
        Boolean isExpired = jwtUtils.isExpired(token);
        if (!isExpired) {
        	response = Collections.singletonMap("message", "Toekn 尚未過期");
        	return ResponseEntity.ok(response);
        }else {
        	throw new CertificateExpiredException("Token 過期");
        }
    }
    
	@Operation(
		summary = "Verify Email Address",
		description = "Verify user's email address using verification code. " +
					  "Users receive a verification code via email during registration. " +
					  "This endpoint validates the code and activates the user's email verification status."
	)
	   @PutMapping("/verifyemail")
	   public ResponseEntity<Map<String, String>> verifyEmail(
	   	@Parameter(description = "Email verification request containing user ID and verification code", required = true)
	   	@Valid @RequestBody VerifyEmailRequest verifyEmailRequest){
    	Map<String, String> response = null;
    	
    	if (userService.verifyEmail(verifyEmailRequest.getUserId(), verifyEmailRequest.getVCode())) {
    		response = Collections.singletonMap("message", "success");
    		return ResponseEntity.ok(response);
    	}else {
    		response = Collections.singletonMap("message", "Verify Code not match");
    		return ResponseEntity.badRequest().body(response);
    	}
    }
    
	@Operation(
		summary = "Resend Verification Code",
		description = "Resend email verification code to user. " +
					  "If the original verification code was lost or expired, " +
					  "this endpoint generates and sends a new verification code to the user's email address."
	)
	   @PutMapping("/resendverifycode")
	   public ResponseEntity<Map<String, String>> resendVerifyCode(
	   	@Parameter(description = "Request containing user ID for resending verification code", required = true)
	   	@Valid @RequestBody ReSendVerifyCodeRequest reSendVerifyCodeRequest){
    	Map<String, String> response = null;
    	
    	if (userService.reSendVerifyCode(reSendVerifyCodeRequest.getUserId())) {
    		response = Collections.singletonMap("message", "success");
    		return ResponseEntity.ok(response);
    	}else {
    		response = Collections.singletonMap("message", "resend verify code fail");
    		return ResponseEntity.badRequest().body(response);
    	}
    }
    
//	@Autowired
//	private AuthService authService;
//
//	@PostMapping
//	public ResponseEntity<?> getToken(@Valid @RequestBody AuthRequest request) {
//      return authService.getToken(request);
//	}
}
