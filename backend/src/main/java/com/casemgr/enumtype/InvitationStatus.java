package com.casemgr.enumtype;

// Status for the invitation reward payment
public enum InvitationStatus {
    PENDING, // Reward pending (e.g., invited user needs to complete an action)
    PAID,    // Reward paid
    CANCELLED // Reward cancelled (e.g., invitation invalid or expired)
    // Consider adding FAILED if payment can fail
}