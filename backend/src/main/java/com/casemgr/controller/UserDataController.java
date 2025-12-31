package com.casemgr.controller;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.casemgr.converter.UserConverter;
import com.casemgr.entity.Account;
import com.casemgr.entity.User;
import com.casemgr.exception.BusinessException;
import com.casemgr.request.AuthRequest;
import com.casemgr.request.BusinessProfileRequest;
import com.casemgr.request.IntroContentRequest;
import com.casemgr.request.IntroTitleRequest;
import com.casemgr.request.UrlRequest;
import com.casemgr.request.PersonProfileRequest;
import com.casemgr.request.UserTypeRequest;
import com.casemgr.response.FiledataResponse;
import com.casemgr.response.IntroResponse;
import com.casemgr.response.MessageResponse;
import com.casemgr.response.UserResponse;
import com.casemgr.service.impl.ProfileService;
import com.casemgr.service.impl.UserServiceImpl;
import com.casemgr.utils.JwtUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "User Profile API", description = "APIs for managing user profile information, account settings, and personal data")
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping({ "/api/profile" })
@Slf4j
public class UserDataController {

	private final UserServiceImpl userService;
	private final ProfileService profileService;
	private final JwtUtils jwtUtils;
	@PutMapping("/changepassword")
	@Operation(
		summary = "Change Password",
		description = "Change user's current password. Requires current username and new password. " +
					  "User must be authenticated to perform this operation."
	)
	public ResponseEntity<MessageResponse> changePwd(
		@Parameter(description = "Authentication request containing username and new password", required = true)
		@Valid @RequestBody AuthRequest request) {
		try {
			userService.changePassword(request);
		} catch (NoSuchAlgorithmException | BusinessException e) {
			e.printStackTrace();
			throw new InternalError("Change password exception");
		}
		return ResponseEntity.ok(new MessageResponse("Change password success!"));
	}

	@PutMapping("/changeusertype")
	@Operation(
		summary = "Change User Type",
		description = "Change user's account type between CLIENT and PROVIDER. " +
					  "This affects the user's permissions and available features in the system. " +
					  "Valid types are: 'CLIENT' (for service buyers) and 'PROVIDER' (for service providers)."
	)
	public ResponseEntity<MessageResponse> changeUserType(
		@Parameter(description = "User type change request containing the new user type (CLIENT or PROVIDER)", required = true)
		@Valid @RequestBody UserTypeRequest request) {
		userService.changeUserType(request);
		return ResponseEntity.ok(new MessageResponse("Change UserType success!"));
	}
	
	@PutMapping("/personal")
	@Operation(
		summary = "Update Personal Profile",
		description = "Update user's personal profile information including name, contact details, " +
					  "personal description, and other individual-specific data. " +
					  "This is typically used for personal account holders."
	)
	public ResponseEntity<MessageResponse> updateUserData(
		@Parameter(description = "Personal profile update request containing personal information fields", required = true)
		@Valid @RequestBody PersonProfileRequest request) {
		profileService.updatePersonProfile(request);
		return ResponseEntity.ok(new MessageResponse("Update personal profile success!"));
	}
	
	@PutMapping("/business")
	@Operation(
		summary = "Update Business Profile",
		description = "Update user's business profile information including company name, business description, " +
					  "industry classification, business registration details, and other company-specific data. " +
					  "This is typically used for business account holders."
	)
	public ResponseEntity<MessageResponse> updateBusinessData(
		@Parameter(description = "Business profile update request containing business information fields", required = true)
		@Valid @RequestBody BusinessProfileRequest request) {
		profileService.updateBunissProfile(request);
		return ResponseEntity.ok(new MessageResponse("Update business profile success!"));
	}
	
	@GetMapping
	@Operation(
		summary = "Get Current User Profile",
		description = "Retrieve the complete profile information of the currently authenticated user. " +
					  "Returns user details, account settings, profile data, and associated information. " +
					  "Requires valid authentication token."
	)
	public ResponseEntity<UserResponse> me() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
//		UserResponse userResponse =  UserConverter.INSTANCT.entityToResponse(user);
		return ResponseEntity.ok(UserConverter.INSTANCT.entityToResponse(user));
	}

	@GetMapping("/roles")
	@Operation(
		summary = "Get User Roles",
		description = "Retrieve the list of roles assigned to the current user. " +
					  "Roles determine user permissions and access levels within the system. " +
					  "Extracts role information from the JWT token."
	)
	public ResponseEntity<List<String>> getRoles(
		@Parameter(description = "HTTP headers containing Authorization header with Bearer token", required = true)
		@RequestHeader Map<String, String> header) {
		String authorization = header.get("authorization");
		String token = authorization.split(" ")[1];
		log.info("Jwt Token: {}", token);
		String strRoles = jwtUtils.extractRoles(token);
		return ResponseEntity.ok(Arrays.asList(strRoles.split(",")));
	}
	
	@GetMapping("/intro/{uId}")
	@Operation(
		summary = "Get User Introduction Profile",
		description = "Retrieve a user's public introduction profile by user ID. " +
					  "This includes introduction title, content, video, signature, and other public-facing information. " +
					  "Used for displaying user profiles to other users."
	)
	public ResponseEntity<IntroResponse> getIntroProfile(
		@Parameter(description = "User ID to retrieve introduction profile for", required = true)
		@PathVariable Long uId) {
		User user = userService.getUser(uId);
		return ResponseEntity.ok(UserConverter.INSTANCT.entityToIntroResponse(user));
	}
	
	@PostMapping("/intro/title")
	@Operation(
		summary = "Update Introduction Title",
		description = "Update the title of the current user's introduction profile. " +
					  "This title appears as a headline in the user's public profile and should be concise and descriptive."
	)
	public ResponseEntity<IntroResponse> updateIntroTitle(
		@Parameter(description = "Request containing the new introduction title", required = true)
		@RequestBody IntroTitleRequest introReq) {
		return ResponseEntity.ok(profileService.updateIntroTitle(introReq.getTitle()));
	}
	
	@PostMapping("/intro/content")
	@Operation(
		summary = "Update Introduction Content",
		description = "Update the detailed content of the current user's introduction profile. " +
					  "This content provides a comprehensive description of the user's background, skills, and services."
	)
	public ResponseEntity<IntroResponse> updateIntroContent(
		@Parameter(description = "Request containing the new introduction content", required = true)
		@RequestBody IntroContentRequest introReq) {
		return ResponseEntity.ok(profileService.updateIntroContent(introReq.getContent()));
	}
	
	@PostMapping(value = "/intro/video", consumes = "multipart/*" , headers = "content-type=multipart/form-data")
	@Operation(
		summary = "Upload Introduction Video File",
		description = "Upload a video file for the user's introduction profile. " +
					  "The video will be stored and can be displayed on the user's public profile. " +
					  "Supports common video formats. File size limits may apply."
	)
	public ResponseEntity<IntroResponse> updateIntroVideo(
		@Parameter(description = "Video file to upload for introduction", required = true)
		@RequestParam("file") MultipartFile file) {
		return ResponseEntity.ok(profileService.updateIntroVideo(file));
	}
	
	@PostMapping(value = "/intro/videourl")
	@Operation(
		summary = "Set Introduction Video URL",
		description = "Set the introduction video using an external URL (e.g., YouTube, Vimeo). " +
					  "The URL should point to a publicly accessible video that will be embedded in the user's profile."
	)
	public ResponseEntity<IntroResponse> updateIntroVideoUrl(
		@Parameter(description = "Request containing the video URL", required = true)
		@RequestBody UrlRequest introReq) {
		return ResponseEntity.ok(profileService.updateIntroVideo(introReq.getFileUrl()));
	}
	
	@PostMapping(value = "/intro/signature" , consumes = "multipart/form-data")
	@Operation(
		summary = "Upload Digital Signature",
		description = "Upload a digital signature image for the user's profile. " +
					  "This signature can be used in contracts and official documents. " +
					  "Should be a clear image file (PNG, JPG) with transparent background preferred."
	)
	public ResponseEntity<IntroResponse> uploadSignature(
		@Parameter(description = "Signature image file to upload", required = true)
		@RequestParam("file") MultipartFile file) {
		return ResponseEntity.ok(profileService.updateSignature(file));
	}
	
	@PostMapping(value = "/intro/paymentaccount")
	@Operation(
		summary = "Update Payment Account",
		description = "Update the user's payment account information for receiving payments. " +
					  "This includes bank account details, payment processor information, or other payment methods. " +
					  "Used when the user provides services and needs to receive payments."
	)
	public ResponseEntity<IntroResponse> updatePaymentAccount(
		@Parameter(description = "Payment account information including account details and payment method", required = true)
		@RequestBody Account paymentAccount) {
		return ResponseEntity.ok(profileService.updatePaymentAccount(paymentAccount));
	}
	
	@PostMapping(value = "/intro/receivingaccount")
	@Operation(
		summary = "Update Receiving Account",
		description = "Update the user's receiving account information for collecting payments from clients. " +
					  "This account is used for receiving funds from completed orders and services. " +
					  "Includes account verification and security measures."
	)
	public ResponseEntity<IntroResponse> updateReceivingAccount(
		@Parameter(description = "Receiving account information including account details and verification data", required = true)
		@RequestBody Account receivingAccount) {
		return ResponseEntity.ok(profileService.updateReceivingAccount(receivingAccount));
	}
}
