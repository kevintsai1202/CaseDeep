package com.casemgr.response;

import java.util.List;

import com.casemgr.entity.BusinessProfile;
import com.casemgr.entity.PersonProfile;
import com.casemgr.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
	private Long userId;
	private String username;
	private String email;
	private List<Role> roles;
	private String userType;
	private PersonProfile personProfile;
	private BusinessProfile businessProfile;
	private String title;
	private String vedioUrl;
	private String signatureUrl;
	private String content;
}
