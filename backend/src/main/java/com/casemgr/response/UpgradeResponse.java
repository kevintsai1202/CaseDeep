package com.casemgr.response;

import java.time.LocalDateTime;
import java.util.Optional;

import com.casemgr.entity.Upgrade;
import com.casemgr.entity.PersonProfile;
import com.casemgr.entity.User;
import com.casemgr.enumtype.UpgradeStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpgradeResponse {

    private String upgradeIdStr; // e.g., UP2501062582
    private Double amount; // Consider BigDecimal
    private LocalDateTime date;
    private UpgradeStatus status;
    private String username;
    private String userDisplayName; // Assuming User has a display name field or logic

    public UpgradeResponse(Upgrade upgrade) {
        this.upgradeIdStr = upgrade.getUpgradeIdStr();
        this.amount = upgrade.getAmount();
        this.date = upgrade.getDate();
        this.status = upgrade.getStatus();
        User user = upgrade.getUser();
        this.username = (user != null) ? user.getUsername() : null;
        // Get display name: Try PersonProfile.legalFullName, fallback to User.username
        this.userDisplayName = Optional.ofNullable(user)
                                     .flatMap(u -> Optional.ofNullable(u.getPersonProfile())) // Use flatMap for nested Optional
                                     .map(PersonProfile::getLegalFullName) // Use legalFullName
                                     .filter(name -> name != null && !name.isEmpty()) // Ensure name is not blank
                                     .orElse(this.username != null ? this.username : "N/A"); // Fallback to username or N/A
    }
}