package com.casemgr.service.impl;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.casemgr.converter.ChatUserConverter;
import com.casemgr.converter.UserConverter;
import com.casemgr.entity.AdminRoleIndustryScope;
import com.casemgr.entity.BusinessProfile;
import com.casemgr.entity.Industry;
import com.casemgr.entity.PersonProfile;
import com.casemgr.entity.Role;
import com.casemgr.entity.User;
import com.casemgr.enumtype.StatusType;
import com.casemgr.enumtype.UserType;
import com.casemgr.exception.BusinessException;
import com.casemgr.repository.AdminRoleIndustryScopeRepository;
import com.casemgr.repository.IndustryRepository;
import com.casemgr.repository.RoleRepository;
import com.casemgr.repository.UserRepository;
import com.casemgr.request.AuthRequest;
import com.casemgr.request.RegisterRequest;
import com.casemgr.request.UserTypeRequest;
import com.casemgr.request.RoleIndustryScopeRequest;
import com.casemgr.response.*;
import com.casemgr.request.AdminUserCreateRequest;
import com.casemgr.request.AdminUserUpdateRequest;
import com.casemgr.specification.UserSpecification;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private AdminRoleIndustryScopeRepository adminRoleIndustryScopeRepository;
	
	@Autowired
	private IndustryRepository industryRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public User createAdminUser(AdminUserCreateRequest createRequest) {
		User user = new User();
		user.setUsername(createRequest.getUsername());
		user.setPassword(passwordEncoder.encode(createRequest.getPassword()));
		user.setEmail(createRequest.getEmail());
		user.setUserType(createRequest.getUserType());
		user.setRegion(createRequest.getRegion());
		// Assuming roles are fetched and set based on roleNames
		Set<Role> roles = roleRepository.findByRoleNameIn(createRequest.getRoleNames());
		user.setRoles(roles);
		// user.setStatus(StatusType.OFFLINE); // Default status
		user.setSendVerifyFlag(true); // Assuming admin-created users are pre-verified or verification is handled differently
		user.setSendResetFlag(true);
		user = userRepository.save(user);
		
		// 處理角色產業範圍
		if (createRequest.getRoleIndustryScopes() != null && !createRequest.getRoleIndustryScopes().isEmpty()) {
			saveRoleIndustryScopes(user, createRequest.getRoleIndustryScopes());
		}
		
		return user;
	}
	
	/**
	 * 儲存角色產業範圍
	 * @param user 用戶實體
	 * @param roleIndustryScopes 角色產業範圍請求列表
	 */
	private void saveRoleIndustryScopes(User user, List<RoleIndustryScopeRequest> roleIndustryScopes) {
		for (RoleIndustryScopeRequest scopeRequest : roleIndustryScopes) {
			Role role = roleRepository.findByRoleName(scopeRequest.getRoleName())
					.orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + scopeRequest.getRoleName()));
			
			if (scopeRequest.getIsAllIndustries()) {
				AdminRoleIndustryScope scope = new AdminRoleIndustryScope();
				scope.setUser(user);
				scope.setRole(role);
				scope.setIsAllIndustries(true);
				adminRoleIndustryScopeRepository.save(scope);
			} else if (scopeRequest.getIndustryIds() != null && !scopeRequest.getIndustryIds().isEmpty()) {
				for (Long industryId : scopeRequest.getIndustryIds()) {
					Industry industry = industryRepository.findById(industryId)
							.orElseThrow(() -> new EntityNotFoundException("Industry not found with id: " + industryId));
					AdminRoleIndustryScope scope = new AdminRoleIndustryScope();
					scope.setUser(user);
					scope.setRole(role);
					scope.setIndustry(industry);
					scope.setIsAllIndustries(false);
					adminRoleIndustryScopeRepository.save(scope);
				}
			}
		}
	}

	public User updateAdminUser(Long userId, AdminUserUpdateRequest updateRequest) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

		if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
			user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
		}
		if (updateRequest.getUserType() != null) {
			user.setUserType(updateRequest.getUserType());
		}
		if (updateRequest.getRegion() != null) {
			user.setRegion(updateRequest.getRegion());
		}
		if (updateRequest.getRoleNames() != null && !updateRequest.getRoleNames().isEmpty()) {
			Set<Role> roles = roleRepository.findByRoleNameIn(updateRequest.getRoleNames());
			user.setRoles(roles);
		}
		user = userRepository.save(user);
		
		// 處理角色產業範圍
		if (updateRequest.getRoleIndustryScopes() != null && !updateRequest.getRoleIndustryScopes().isEmpty()) {
			adminRoleIndustryScopeRepository.deleteByUser_uId(userId);
			saveRoleIndustryScopes(user, updateRequest.getRoleIndustryScopes());
		}
		
		return user;
	}

	@Transactional
	public void deleteUser(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
		// Soft delete by setting status to INACTIVE or DELETED, or implement actual deletion
		// For this example, let's assume a status field for soft delete
		 // Or StatusType.INACTIVE
		 userRepository.delete(user);
		// Alternatively, for hard delete:
		// userRepository.deleteById(userId);
	}


//	@Autowired
//	private VerifyCodeServiceImpl verifyCodeService;
	
//	@Autowired
//	FileStorageServiceImpl fileStorageService;

//	@Autowired
//	private JavaMailSender mailSender;

//	public UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//        this.passwordEncoder = new BCryptPasswordEncoder();
//    }

//	@PostConstruct
//	public void init() {
//		initAllUserStatus();
//	}
	
//    private final Random random = new Random();

    private String generateVerifyCode() {
    	Random random = new Random();
        int code = 1000 + random.nextInt(9000); // 生成4位數的驗證碼
        return String.valueOf(code);
    }

	@Transactional
	public void generateResetCode(User user) {
//		User user = userRepository.findByEmail(email);
//		if (user == null) {
//			throw new UsernameNotFoundException("User not found with email: " + email);
//		}

		String resetCode = UUID.randomUUID().toString();
		user.setResetCode(resetCode);
		user.setResetExpiryDate(LocalDateTime.now().plusMinutes(1));
		user.setSendResetFlag(false);
		userRepository.save(user);

//		sendResetEmail(user);
	}

//	private void sendResetEmail(User user) {
//		String resetUrl = "http://casedeep.com/resetpassword?resetcode=" + user.getResetCode();
//
//		String context = String.format("Dear [%s]\r\n"
//				+ "We received a request to reset your password. Please click the link below to reset your password:\r\n"
//				+ "%s" + "\r\n"
//				+ "Please note that this link is valid for 60 seconds only. If you do not reset your password within this time frame, you will need to request a new password reset link.\r\n"
//				+ "If you did not request this change, please ignore this email, and your password will remain unchanged.\r\n"
//				+ "\r\n" + "Thank you!\r\n" + "CodeTest Team", user.getUsername(), resetUrl);
//		SimpleMailMessage message = new SimpleMailMessage();
//		message.setTo(user.getEmail());
//		message.setFrom("support@casedeep.com");
//		message.setSubject("Password Reset Request");
//		message.setText(context);
//		mailSender.send(message);
//	}

	@Transactional
	public void resetPassword(String resetCode, String newPassword) throws BusinessException {
		User user = userRepository.findByResetCode(resetCode);
		if (user == null || user.getResetExpiryDate().isBefore(LocalDateTime.now())) {
			throw new BusinessException("Invalid or expired token.");
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		user.setResetCode(null);
		user.setResetExpiryDate(null);
		userRepository.save(user);
	}

	@Transactional
	public User createUser(RegisterRequest registerRequest) throws NoSuchAlgorithmException {
		User user = UserConverter.INSTANCT.requestToEntity(registerRequest);
		user.setUserType(UserType.CLIENT);
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

		String vCode = generateVerifyCode();
		user.setSendVerifyFlag(false);
		user.setSendResetFlag(true);
		user.setVerifyCode(vCode);

//		log.info("new User:" + user);
//		verifyCodeService.sendVerifyCode(user, vCode);

		userRepository.save(user);
		return userRepository.save(user);
	}

//    public User getMe() {
//    	User user = null;
//    	Object object = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//    	if (object instanceof UserDetails) {
//    		user = (User) object;
//    	}
//    	return user;
//    }

	
	
	@Transactional
	public User changeUserType( UserTypeRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		user.setUserType(request.getUserType());
		userRepository.save(user);
		return userRepository.save(user);
	}

	@Transactional
	public User changePassword(AuthRequest changePasswordRequest) throws NoSuchAlgorithmException, BusinessException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		if (user.getUsername().equals(changePasswordRequest.getUsername())) {
			user.setPassword(passwordEncoder.encode(changePasswordRequest.getPassword()));
			userRepository.save(user);
			return userRepository.save(user);
		}else {
			throw new BusinessException("Username not match!");
		}
	}
	
	
	

	@Transactional
	public boolean reSendVerifyCode(Long id) {
		User user = userRepository.getReferenceById(id);
		String vCode = generateVerifyCode();
		user.setVerifyCode(vCode);
		user.setSendVerifyFlag(false);
		userRepository.save(user);
//		verifyCodeService.sendVerifyCode(user, vCode);
		return true;
	}

	@Transactional
	public boolean verifyEmail(Long id, String vCode) {
		User user = userRepository.getReferenceById(id);
		String serverVCode = user.getVerifyCode();
		if (serverVCode.equals(vCode)) {
			user.setVerifyCode("");
			Role userRole = roleRepository.findByRoleName("ROLE_USER")
					.orElseThrow(() -> new EntityNotFoundException("Default user role not found"));
			Set<Role> roles = user.getRoles();
			roles.add(userRole);
			user.setRoles(roles);
			userRepository.save(user);
			return true;
		} else {
			return false;
		}
	}

	public List<User> getAllUser() {
		return userRepository.findAll();
	}

	public User getUser(Long id) {
		Optional<User> instance = userRepository.findById(id);
		if (instance.isPresent()) {
			return instance.get();
		} else {
			throw new EntityNotFoundException();
		}
	}

	public ChatUser getChatUser(Long id) {
		User dbUser = userRepository.getReferenceById(id);
//		System.out.println("dbUser:" + dbUser);
		ChatUser chatUser = new ChatUser(dbUser.getUId(), dbUser.getUsername(), dbUser.getStatus());
		System.out.println("chatUser:" + chatUser);
		return chatUser;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> instance = userRepository.findByUsername(username);
		if (instance.isPresent()) {
			return instance.get();
		}
		throw new UsernameNotFoundException("username not found");
	}

	@Transactional
	public ChatUser connectUser(Long uId) {
		User dbUser = (User) getUser(uId);
		System.out.println("user:" + dbUser);
		dbUser.setStatus(StatusType.ONLINE);
		userRepository.save(dbUser);
		return new ChatUser(dbUser.getUId(), dbUser.getUsername(), StatusType.ONLINE);
	}

	@Transactional
	public ChatUser disconnectUser(Long uId) {
		User dbUser = (User) getUser(uId);
		System.out.println("user:" + dbUser);
		dbUser.setStatus(StatusType.OFFLINE);
		userRepository.save(dbUser);
		return new ChatUser(dbUser.getUId(), dbUser.getUsername(), StatusType.OFFLINE);
	}

	public List<ChatUser> listAllChatUsers() {
		return ChatUserConverter.INSTANCE.entityToResponse(userRepository.findAll());
	}

	public List<ChatUser> findConnectedUsers() {
		return ChatUserConverter.INSTANCE.entityToResponse(userRepository.findAllByStatus(StatusType.ONLINE));
	}

	public List<ChatUser> findDisconnectedUsers() {
		return ChatUserConverter.INSTANCE.entityToResponse(userRepository.findAllByStatus(StatusType.OFFLINE));
	}

	@Transactional
	public void initAllUserStatus() {
		userRepository.updateAllUserStatus(StatusType.OFFLINE.ordinal());
	}
}