package com.casemgr.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.casemgr.entity.Certification;
import com.casemgr.entity.Fee;
import com.casemgr.entity.FeeCode;
import com.casemgr.entity.Filedata;
import com.casemgr.entity.User;
import com.casemgr.enumtype.CEStatus;
import com.casemgr.enumtype.FeeType;
import com.casemgr.exception.BusinessException;
import com.casemgr.repository.CertificationRepository;
import com.casemgr.repository.FeeCodeRepository;
import com.casemgr.repository.FiledataRepository;
import com.casemgr.repository.UserRepository;
import com.casemgr.response.CertificationResponse;
import com.casemgr.service.CertificationService;
import com.casemgr.utils.Base62Utils;
import com.casemgr.utils.NumberUtils;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificationServiceImpl implements CertificationService{
	
	private final CertificationRepository certificationRepository;
	private final FiledataRepository filedataRepository;
	private final UserRepository userRepository;
	private final FeeCodeRepository feeCodeRepository;
	
	public CertificationResponse updateCertification(Certification certification) {
		certification = certificationRepository.save(certification);
		
		return new CertificationResponse(certification);
	}
	
	public CertificationResponse getCertification(String ceNoBase62) {
		String ceNo = Base62Utils.decode(ceNoBase62);
		Certification ce = certificationRepository.findByCeNo(ceNo);
		return new CertificationResponse(ce);
	}
	
	public CertificationResponse createCE() throws EntityExistsException{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		List<Certification> ces = certificationRepository.findByApplicantAndStatusNot(user, CEStatus.Reject);
		if ((ces != null) && (ces.size() > 0))
			throw new EntityExistsException("Certification Application in Progress");
		Certification ce = new Certification();
		ce.setApplicant(user);
		ce.setCeNo(NumberUtils.generateFormNumber("CE"));
		ce.setStatus(CEStatus.Create);
		ce = certificationRepository.save(ce);
		
		return new CertificationResponse(ce);
	}
	
//	public CertificationResponse updateFile(String ceNoBase62, String fileUrl) throws BusinessException{
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		User user = (User) auth.getPrincipal();
//		String ceNo = Base62Utils.decode(ceNoBase62);
//		Certification ce = certificationRepository.findByCeNo(ceNo);
////		if (ce.getAuditor() != null) {
////			throw new BusinessException("Some one accepted!");
////		}
//		if (!ce.getStatus().equals(CEStatus.Create)) {
//			throw new BusinessException("Status not Create");
//		}
//		ce.setUploadUrl(fileUrl);
//		ce = certificationRepository.save(ce);
//		
//		return new CertificationResponse(ce);
//	}
	
	public CertificationResponse uploadDeal(String ceNoBase62, Filedata fileData) throws BusinessException{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		String ceNo = Base62Utils.decode(ceNoBase62);
		Certification ce = certificationRepository.findByCeNo(ceNo);
//		if (ce.getAuditor() != null) {
//			throw new BusinessException("Some one accepted!");
//		}
		if (!ce.getStatus().equals(CEStatus.Create)) {
			throw new BusinessException("Status not Open");
		}
		fileData.setDeal(ce);
		filedataRepository.save(fileData);
		return new CertificationResponse(ce);
	}
	
	public CertificationResponse uploadIdentity(String ceNoBase62, Filedata fileData) throws BusinessException{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		String ceNo = Base62Utils.decode(ceNoBase62);
		Certification ce = certificationRepository.findByCeNo(ceNo);
		if (ce.getAuditor() != null) {
			throw new BusinessException("Some one accepted!");
		}
		if (!ce.getStatus().equals(CEStatus.Create)) {
			throw new BusinessException("Status not Open");
		}
		fileData.setIdentity(ce);
		filedataRepository.save(fileData);
		
		return new CertificationResponse(ce);
	}
	
	public CertificationResponse submitCE(String ceNoBase62) throws BusinessException{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		String ceNo = Base62Utils.decode(ceNoBase62);
		
		Certification ce = certificationRepository.findByCeNo(ceNo);
		if (!ce.getApplicant().getUId().equals(user.getUId())) {
			throw new BusinessException("Not Creator");
		}
		ce.setStatus(CEStatus.Submit);
		ce = certificationRepository.save(ce);
		return new CertificationResponse(ce);
	}
	
	public CertificationResponse acceptCE(String ceNoBase62) throws BusinessException{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		String ceNo = Base62Utils.decode(ceNoBase62);
		Certification ce = certificationRepository.findByCeNo(ceNo);
//		if (ce.getAuditor() != null) {
//			throw new BusinessException("Some one accepted!");
//		}
		if (!ce.getStatus().equals(CEStatus.Submit)) {
			throw new BusinessException("Status not Open");
		}
		ce.setAuditor(user);
		ce.setStatus(CEStatus.Process);
		ce = certificationRepository.save(ce);
		
		return new CertificationResponse(ce);
	}
	
	public CertificationResponse approveCE(String ceNoBase62, Double score) throws BusinessException{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User auditor = (User) auth.getPrincipal();
		String ceNo = Base62Utils.decode(ceNoBase62);
		Certification ce = certificationRepository.findByCeNo(ceNo);
		log.info("auditor:{}", auditor);
//		log.info("auditor:{}",ce.getAuditor());
//		if (!ce.getAuditor().getUId().equals(user.getUId())) {
//			throw new BusinessException("Auditor not me");
//		}
		
		if (!ce.getStatus().equals(CEStatus.Submit)) {
			throw new BusinessException("Status not submit");
		}
		
		User applicant = ce.getApplicant();
//		applicant.setCertified(true);
		applicant.setInitScore(score);
		userRepository.save(applicant);
		
		ce.setAuditor(auditor);
		ce.setScore(score);
		ce.setStatus(CEStatus.Approve);
		ce = certificationRepository.save(ce);
		
		return new CertificationResponse(ce);
	}
	
	public CertificationResponse rejectCE(String ceNoBase62, String comment) throws BusinessException{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User auditor = (User) auth.getPrincipal();
		String ceNo = Base62Utils.decode(ceNoBase62);
		Certification ce = certificationRepository.findByCeNo(ceNo);
//		if (!ce.getAuditor().getUId().equals(user.getUId())) {
//			throw new BusinessException("Auditor not me");
//		}
//		if (!ce.getStatus().equals(CEStatus.Submit)) {
//			throw new BusinessException("Status not Submit");
//		}
		
		ce.setAuditor(auditor);
		ce.setStatus(CEStatus.Reject);
		ce = certificationRepository.save(ce);
		return new CertificationResponse(ce);
	}
	
	public CertificationResponse inputFeeCode(String ceNoBase62, String code) throws BusinessException {
		FeeCode feeCode = feeCodeRepository.findByFeeCode(code);
		String ceNo = Base62Utils.decode(ceNoBase62);
		
		if ((feeCode != null) && feeCode.getFeeCode().length()>0) {
			Certification ce = certificationRepository.findByCeNo(ceNo);
			
			if (!ce.getStatus().equals(CEStatus.Approve)) {
				throw new BusinessException("Status not approve");
			}
			
			ce.setFeeCode(feeCode);
			ce.setStatus(CEStatus.Paid);
			return new CertificationResponse(certificationRepository.save(ce));
		}else {
			throw new BusinessException("Invalaid Fee Code!");
		}
	}
	
	public CertificationResponse payFee(String ceNoBase62, Double amount) throws BusinessException {
		String ceNo = Base62Utils.decode(ceNoBase62);
		Certification ce = certificationRepository.findByCeNo(ceNo);
		
		if (!ce.getStatus().equals(CEStatus.Approve)) {
			throw new BusinessException("Status not approve");
		}
		Fee fee = new Fee();
		fee.setAmount(amount);
		fee.setFeeType(FeeType.Certification);
		ce.setFee(fee);
		ce.setStatus(CEStatus.Paid);
		return new CertificationResponse(certificationRepository.save(ce));
	}
	
	public CertificationResponse signCE(String ceNoBase62, String signName) throws BusinessException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		String ceNo = Base62Utils.decode(ceNoBase62);
		Certification ce = certificationRepository.findByCeNo(ceNo);
		
		if (!ce.getApplicant().getUId().equals(user.getUId())) {
			throw new BusinessException("Applicant not me");
		}
		
		if (!ce.getStatus().equals(CEStatus.Paid)) {
			throw new BusinessException("Status not paid");
		}
		
		user.setCertified(true);
		userRepository.save(user);
		
		ce.setSignName(signName);
		ce.setStatus(CEStatus.Complete);
		return new CertificationResponse(certificationRepository.save(ce));
	}
	
	public Double setCommissionRate(String ceNoBase62, Double rate) throws BusinessException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		String ceNo = Base62Utils.decode(ceNoBase62);
		Certification ce = certificationRepository.findByCeNo(ceNo);
		
		if (!ce.getApplicant().getUId().equals(user.getUId())) {
			throw new BusinessException("Applicant not me");
		}
		
		if (!ce.getStatus().equals(CEStatus.Complete)) {
			throw new BusinessException("Status not Complete");
		}
		
		if (user.getCertified() != true) {
			throw new BusinessException("Not yet certified");
		}
		
		user.setCommissionRate(rate);
		user = userRepository.save(user);
		
		return user.getCommissionRate();
	}
	
	public List<CertificationResponse> listSubmitCertification(){
		List<Certification> ces = certificationRepository.findByStatus(CEStatus.Submit);
		return ces.stream().map(CertificationResponse::new).toList();
	}
	
	public List<CertificationResponse> listAllCertification(){
		List<Certification> ces = certificationRepository.findAll();
		return ces.stream().map(CertificationResponse::new).toList();
	}
	
	public List<CertificationResponse> listMyCertification(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		log.info("User:{}",user.getUsername());
		List<Certification> ces = certificationRepository.findByApplicant(user);
		return ces.stream().map(CertificationResponse::new).toList();
	}

	@Override
    @Transactional
    public Page<CertificationResponse> listCertificationsForAdmin(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Page.empty(pageable);
        }

        User currentUser = (User) auth.getPrincipal();

        boolean isSuperAdmin = currentUser.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPER_ADMIN"));

        Page<Certification> certificationPage;
        if (isSuperAdmin) {
            // Super Admin sees all certifications
            certificationPage = certificationRepository.findAll(pageable);
        } else {
            // Regional admin sees certifications for applicants in their region
            String adminRegion = currentUser.getRegion();
            if (adminRegion == null || adminRegion.isEmpty()) {
                log.warn("Admin user {} has no region assigned, cannot filter certifications.", currentUser.getUsername());
                return Page.empty(pageable);
            }
            // Need to add findByApplicant_Region method to CertificationRepository
            certificationPage = certificationRepository.findByApplicant_Region(adminRegion, pageable);
        }

        // Convert Page<Certification> to Page<CertificationResponse>
        return certificationPage.map(CertificationResponse::new);
    }

    @Override
    @Transactional
    public CertificationResponse updateCertificationByAdmin(Long certificationId, CEStatus newStatus, String reviewComment, Double score) throws EntityNotFoundException, BusinessException {
        Certification certification = certificationRepository.findById(certificationId)
            .orElseThrow(() -> new EntityNotFoundException("Certification not found with id: " + certificationId));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User adminUser = (User) auth.getPrincipal();

        // TODO: Add more specific authorization checks if needed (e.g., based on region or specific roles)

        CEStatus oldStatus = certification.getStatus();
        log.info("Admin {} updating certification ID: {} from status {} to {}. Comment: {}, Score: {}",
                 adminUser.getUsername(), certificationId, oldStatus, newStatus, reviewComment, score);

        // TODO: Add validation for allowed status transitions by admin

        certification.setStatus(newStatus);
        certification.setAuditor(adminUser); // Set the admin who performed the update as reviewer

        if (reviewComment != null) {
            // Append to history or set comment based on requirements
            // For now, let's just set the rejectComment field if rejecting
            if (CEStatus.Reject.equals(newStatus)) {
                 certification.setRejectComment(reviewComment);
            }
            // Consider adding a dedicated reviewHistory field update here
        }

        if (score != null && CEStatus.Approve.equals(newStatus)) {
            certification.setScore(score);
            // Also update user's initScore if this is the approval action
            User applicant = certification.getApplicant();
            if (applicant != null) {
                applicant.setInitScore(score);
                userRepository.save(applicant);
            }
        }

        certification = certificationRepository.save(certification);
        return new CertificationResponse(certification);
    }
}
