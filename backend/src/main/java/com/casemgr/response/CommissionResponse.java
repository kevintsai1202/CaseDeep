package com.casemgr.response;

import java.time.LocalDateTime;
import java.util.Optional;

import com.casemgr.entity.Commission;
import com.casemgr.entity.BusinessProfile;
import com.casemgr.entity.Order;
import com.casemgr.entity.User;
import com.casemgr.enumtype.OrderStatus;
import com.casemgr.enumtype.PaymentStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommissionResponse {

    private String commissionIdStr; // e.g., CO2501232305
    private String orderIdStr; // e.g., z2sXfpBvBo5 (Assuming Order has orderNo)
    private String templateName; // From Order -> OrderTemplate
    private Double orderAmount; // From Order -> totalPrice (Consider BigDecimal)
    private Double commissionAmount; // From Commission
    private Double commissionRate; // From Commission
    private PaymentStatus commissionPaymentStatus; // From Commission
    private LocalDateTime commissionPaymentDate; // From Commission
    private LocalDateTime orderDate; // From Order -> createTime (Assuming BaseEntity has createTime)
    private LocalDateTime deliveryDate; // Need logic to determine this (e.g., from DeliveryItem) - Placeholder
    private OrderStatus orderStatus; // From Order
    private String providerUsername; // From Order -> Provider
    private String providerDisplayName; // From Order -> Provider -> Profile
    private String clientUsername; // From Order -> Client
    private String clientDisplayName; // From Order -> Client -> Profile

    public CommissionResponse(Commission commission) {
        this.commissionIdStr = commission.getCommissionIdStr();
        this.commissionAmount = commission.getAmount();
        this.commissionRate = commission.getRate();
        this.commissionPaymentStatus = commission.getPaymentStatus();
        this.commissionPaymentDate = commission.getPaymentDate();

        Order order = commission.getOrder();
        if (order != null) {
            this.orderIdStr = order.getOrderNo(); // Assuming orderNo exists
            this.orderAmount = (order.getTotalPrice() != null) ? order.getTotalPrice().doubleValue() : null; // Convert BigDecimal
            this.orderDate = order.getCreateTime(); // Assuming createTime in BaseEntity
            this.orderStatus = order.getStatus();

            if (order.getOrderTemplate() != null) {
                this.templateName = order.getOrderTemplate().getName();
            }

            User provider = order.getProvider();
            if (provider != null) {
                this.providerUsername = provider.getUsername();
                // Get display name: Try BusinessProfile.legalBusinessName, fallback to User.username
                this.providerDisplayName = Optional.ofNullable(provider.getBusinessProfile())
                                                 .map(BusinessProfile::getLegalBusinessName) // Use legalBusinessName
                                                 .filter(name -> name != null && !name.isEmpty())
                                                 .orElse(this.providerUsername); // Fallback to username
            }

            User client = order.getClient();
            if (client != null) {
                this.clientUsername = client.getUsername();
                 // Get display name: Try BusinessProfile.legalBusinessName, fallback to User.username
                 this.clientDisplayName = Optional.ofNullable(client.getBusinessProfile())
                                                 .map(BusinessProfile::getLegalBusinessName) // Use legalBusinessName
                                                 .filter(name -> name != null && !name.isEmpty())
                                                 .orElse(this.clientUsername); // Fallback to username
            }

            // TODO: Implement logic to get deliveryDate (e.g., from latest DeliveryItem)
            this.deliveryDate = null; // Placeholder
        }
    }
}