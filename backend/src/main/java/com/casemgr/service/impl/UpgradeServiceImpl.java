package com.casemgr.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casemgr.entity.Upgrade;
import com.casemgr.entity.User;
import com.casemgr.repository.UpgradeRepository;
import com.casemgr.repository.UserRepository; // Needed for region filtering
import com.casemgr.response.UpgradeResponse;
import com.casemgr.service.UpgradeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpgradeServiceImpl implements UpgradeService {

    private final UpgradeRepository upgradeRepository;
    private final UserRepository userRepository; // Inject UserRepository

    @Override
    @Transactional(readOnly = true)
    public Page<UpgradeResponse> listUpgradesForAdmin(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Page.empty(pageable);
        }

        User currentUser = (User) auth.getPrincipal();

        boolean isSuperAdmin = currentUser.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPER_ADMIN"));

        Page<Upgrade> upgradePage;
        if (isSuperAdmin) {
            // Super Admin sees all upgrades
            upgradePage = upgradeRepository.findAll(pageable);
        } else {
            // Regional admin sees upgrades for users in their region
            String adminRegion = currentUser.getRegion();
            if (adminRegion == null || adminRegion.isEmpty()) {
                log.warn("Admin user {} has no region assigned, cannot filter upgrades.", currentUser.getUsername());
                return Page.empty(pageable);
            }
            // Need to add findByUser_Region method to UpgradeRepository
            upgradePage = upgradeRepository.findByUser_Region(adminRegion, pageable);
        }

        // Convert Page<Upgrade> to Page<UpgradeResponse>
        // Assuming UpgradeResponse has a constructor that accepts Upgrade entity
        return upgradePage.map(UpgradeResponse::new);
    }

    // Implement other methods from UpgradeService interface as needed
}