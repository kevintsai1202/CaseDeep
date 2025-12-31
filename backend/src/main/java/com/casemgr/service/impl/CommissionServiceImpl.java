package com.casemgr.service.impl;

import java.time.LocalDateTime; // Added import

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casemgr.entity.Commission;
import com.casemgr.entity.Order;
import com.casemgr.entity.User;
import com.casemgr.enumtype.PaymentStatus;
import com.casemgr.repository.CommissionRepository;
import com.casemgr.repository.OrderRepository; // Needed for region filtering via Order
import com.casemgr.repository.UserRepository; // Needed for getting current user
import com.casemgr.response.CommissionResponse;
import com.casemgr.service.CommissionService;
import com.casemgr.utils.NumberUtils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommissionServiceImpl implements CommissionService {

    private final CommissionRepository commissionRepository;
    private final OrderRepository orderRepository; // Inject OrderRepository
    private final UserRepository userRepository; // Inject UserRepository

    @Override
    @Transactional(readOnly = true)
    public Page<CommissionResponse> listCommissionsForAdmin(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Page.empty(pageable);
        }

        User currentUser = (User) auth.getPrincipal();

        boolean isSuperAdmin = currentUser.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPER_ADMIN"));

        Page<Commission> commissionPage;
        if (isSuperAdmin) {
            // Super Admin sees all commissions
            commissionPage = commissionRepository.findAll(pageable);
        } else {
            // Regional admin sees commissions for orders where provider or client is in their region
            String adminRegion = currentUser.getRegion();
            if (adminRegion == null || adminRegion.isEmpty()) {
                log.warn("Admin user {} has no region assigned, cannot filter commissions.", currentUser.getUsername());
                return Page.empty(pageable);
            }
            // Need to add findByOrder_Provider_RegionOrOrder_Client_Region method to CommissionRepository
            commissionPage = commissionRepository.findByOrder_Provider_RegionOrOrder_Client_Region(adminRegion, adminRegion, pageable);
        }

        // Convert Page<Commission> to Page<CommissionResponse>
        // Assuming CommissionResponse has a constructor that accepts Commission entity
        return commissionPage.map(CommissionResponse::new);
    }

    @Override
    @Transactional
    public CommissionResponse updateCommissionStatus(Long commissionId, PaymentStatus newStatus) throws EntityNotFoundException {
        Commission commission = commissionRepository.findById(commissionId)
            .orElseThrow(() -> new EntityNotFoundException("Commission not found with id: " + commissionId));

        // TODO: Add authorization checks if needed (who can update commission status?)

        log.info("Updating commission status for Commission ID: {} to {}", commissionId, newStatus);
        commission.setPaymentStatus(newStatus);

        // Set payment date if status is PAID
        if (PaymentStatus.Paid.equals(newStatus)) {
            commission.setPaymentDate(LocalDateTime.now());
        } else {
            commission.setPaymentDate(null); // Clear date if not paid
        }

        commission = commissionRepository.save(commission);

        // Assuming CommissionResponse has a constructor that accepts Commission entity
        return new CommissionResponse(commission);
    }

    @Override
    @Transactional(readOnly = true)
    public CommissionResponse findByCommissionIdStr(String commissionIdStr) {
        if (commissionIdStr == null || commissionIdStr.trim().isEmpty()) {
            return null;
        }
        
        return commissionRepository.findByCommissionIdStr(commissionIdStr)
            .map(CommissionResponse::new)
            .orElse(null);
    }

    @Override
    @Transactional
    public CommissionResponse createCommission(Long orderId, Double amount, Double rate) {
        // Find the order
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));
        
        // Create new commission
        Commission commission = new Commission();
        commission.setOrder(order);
        commission.setAmount(amount);
        commission.setRate(rate);
        commission.setPaymentStatus(PaymentStatus.Pending); // Default to pending

        // Generate commission ID
        String commissionIdStr = NumberUtils.generateFormNumber("CO");
        commission.setCommissionIdStr(commissionIdStr);
        
        log.info("Creating new commission with ID: {} for order ID: {}", commissionIdStr, orderId);
        
        // Save commission
        commission = commissionRepository.save(commission);
        
        return new CommissionResponse(commission);
    }

    // Implement other methods from CommissionService interface as needed
}