package com.casemgr.service.impl;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.casemgr.entity.Invitation;
import com.casemgr.entity.User;
import com.casemgr.enumtype.InvitationStatus;
import com.casemgr.repository.InvitationRepository;
import com.casemgr.repository.UserRepository;
import com.casemgr.response.InvitationResponse;
import com.casemgr.service.InvitationService;
import com.casemgr.utils.NumberUtils;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvitationServiceImpl implements InvitationService{
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository; // Inject UserRepository
    
    @Override
	@Transactional
	public String getInvitationCode() {
		String invitationCode = "";
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) auth.getPrincipal();
		if ((user.getInvitationCode() == null) || (user.getInvitationCode().length() ==0)) {
			invitationCode = NumberUtils.generateFormNumber(invitationCode);
			
			user.setInvitationCode(invitationCode);
			userRepository.save(user);
		}
		return invitationCode;
	}
    
    
	@Override
    @Transactional
    public Page<InvitationResponse> listInvitationsForAdmin(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Page.empty(pageable);
        }

        User currentUser = (User) auth.getPrincipal();

        boolean isSuperAdmin = currentUser.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPER_ADMIN"));

        Page<Invitation> invitationPage;
        if (isSuperAdmin) {
            // Super Admin sees all invitations
            invitationPage = invitationRepository.findAll(pageable);
        } else {
            // Regional admin sees invitations involving users in their region (either inviting or invited)
            String adminRegion = currentUser.getRegion();
            if (adminRegion == null || adminRegion.isEmpty()) {
                log.warn("Admin user {} has no region assigned, cannot filter invitations.", currentUser.getUsername());
                return Page.empty(pageable);
            }
            // Need to add findByInvitingUser_RegionOrInvitedUser_Region method to InvitationRepository
            invitationPage = invitationRepository.findByInvitingUser_RegionOrInvitedUser_Region(adminRegion, adminRegion, pageable);
        }

        // Convert Page<Invitation> to Page<InvitationResponse>
        // Assuming InvitationResponse has a constructor that accepts Invitation entity
        return invitationPage.map(InvitationResponse::new);
    }

    @Override
    @Transactional
    public InvitationResponse updateInvitationStatus(Long invitationId, InvitationStatus newStatus) throws EntityNotFoundException {
        Invitation invitation = invitationRepository.findById(invitationId)
            .orElseThrow(() -> new EntityNotFoundException("Invitation not found with id: " + invitationId));

        // TODO: Add authorization checks if needed (who can update invitation status?)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal(); // Assuming principal is User

        log.info("Updating invitation status for Invitation ID: {} to {}", invitationId, newStatus);
        invitation.setStatus(newStatus);

        // Set payment date and payer if status is PAID
        if (InvitationStatus.PAID.equals(newStatus)) {
            invitation.setRewardPaymentTime(LocalDateTime.now());
            invitation.setPayer(currentUser); // Set current admin as payer
        } else {
            invitation.setRewardPaymentTime(null);
            invitation.setPayer(null); // Clear payer if not paid
        }

        invitation = invitationRepository.save(invitation);

        // Assuming InvitationResponse has a constructor that accepts Invitation entity
        return new InvitationResponse(invitation);
    }


}
