package com.casemgr.response;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.casemgr.entity.Certification;
import com.casemgr.entity.User;
import com.casemgr.enumtype.CEStatus;
import com.casemgr.utils.Base62Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificationResponse {
	
	public CertificationResponse(Certification ce) {
		this.ceNoBase62 = Base62Utils.encode(ce.getCeNo());
//		this.uploadUrl = ce.getUploadUrl();
		this.status = ce.getStatus();
		this.identities = Optional.ofNullable(ce.getIdentities())
			    .orElse(Collections.emptyList())
			    .stream()
			    .map(FiledataResponse::new)
			    .toList();
		this.deals = Optional.ofNullable(ce.getDeals())
			    .orElse(Collections.emptyList())
			    .stream()
			    .map(FiledataResponse::new)
			    .toList();
		this.applicant = Optional.ofNullable(ce.getApplicant())
			    .map(User::getUsername)
			    .orElse(null);
		this.auditor = Optional.ofNullable(ce.getAuditor())
			    .map(User::getUsername)
			    .orElse(null);
	}

    private String ceNoBase62;
    
//    private String uploadUrl;
    
    private CEStatus status;
    
    private List<FiledataResponse> identities;
    private List<FiledataResponse> deals;
    
    private String applicant;
    private String auditor;
}
