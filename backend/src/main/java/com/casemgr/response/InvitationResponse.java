package com.casemgr.response;

import java.time.LocalDateTime;
import java.util.Optional;

import com.casemgr.entity.BusinessProfile; // Assuming needed for display name
import com.casemgr.entity.Invitation;
import com.casemgr.entity.PersonProfile; // Assuming needed for display name
import com.casemgr.entity.User;
import com.casemgr.enumtype.InvitationStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InvitationResponse {

    private String invitationIdStr; // e.g., IN2501032340
    private Double rewardAmount; // Consider BigDecimal
    private InvitationStatus rewardPaymentStatus; // Renamed from status for clarity
    private LocalDateTime rewardPaymentTime;
    private String invitingUserUsername;
    private String invitingUserDisplayName;
    private String invitedUserUsername;
    private String invitedUserDisplayName;
    private LocalDateTime invitedUserUpgradeTime; // Need logic to determine this - Placeholder
    private String payerUsername; // Admin who paid

    public InvitationResponse(Invitation invitation) {
        this.invitationIdStr = invitation.getInvitationIdStr();
        this.rewardAmount = invitation.getRewardAmount();
        this.rewardPaymentStatus = invitation.getStatus();
        this.rewardPaymentTime = invitation.getRewardPaymentTime();

        User inviting = invitation.getInvitingUser();
        if (inviting != null) {
            this.invitingUserUsername = inviting.getUsername();
            this.invitingUserDisplayName = getDisplayName(inviting);
        }

        User invited = invitation.getInvitedUser();
        if (invited != null) {
            this.invitedUserUsername = invited.getUsername();
            this.invitedUserDisplayName = getDisplayName(invited);
            // TODO: Implement logic to find the upgrade time for the invited user
            this.invitedUserUpgradeTime = null; // Placeholder
        }

        User payer = invitation.getPayer();
        if (payer != null) {
            this.payerUsername = payer.getUsername();
        }
    }

    // Helper method to get display name (similar to other responses)
    private String getDisplayName(User user) {
        if (user == null) return "N/A";

        String displayName = Optional.ofNullable(user.getPersonProfile())
                                   .map(PersonProfile::getLegalFullName)
                                   .filter(name -> name != null && !name.isEmpty())
                                   .orElse(null);

        if (displayName == null) {
            displayName = Optional.ofNullable(user.getBusinessProfile())
                                .map(BusinessProfile::getLegalBusinessName)
                                .filter(name -> name != null && !name.isEmpty())
                                .orElse(user.getUsername()); // Fallback to username
        }
        return displayName;
    }
}