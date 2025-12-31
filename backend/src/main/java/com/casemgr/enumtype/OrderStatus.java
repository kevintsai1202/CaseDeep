package com.casemgr.enumtype;

public enum OrderStatus {
	   // Initial State
	   inquiry, // Keep existing initial state

	   // Confirmation Phase
//	   ConfirmationPending, // Waiting for user input/choices
	   quote_request,      // User B requested a quote
	   quote_sent,           // User A sent a quote
	   quote_accept,

	   // Contract Phase
//	   ContractPending,         // Confirmation done, waiting for contract actions
//	   ContractChangeRequested, // A change to the contract terms was requested
//	   ContractSigned,          // Contract signed by both parties (or skipped)

	   // Payment Phase
	   awaiting_payment, // Contract done, waiting for payment(s)
//	   PartiallyPaid,  // At least one payment made, but not all
	   in_progress,      // All payments completed

	   // Delivery Phase
//	   DeliveryPending,     // Payment done, waiting for delivery/deliveries
//	   PartiallyDelivered,  // At least one item delivered, but not all accepted
//	   FullyDelivered,      // All items delivered, awaiting acceptance or rating
//	   ModificationRequested, // Client requested modification on a delivered item
	   
	   delivered,
	   
	   in_revision,
	   // Rating Phase
//	   RatingPending, // All deliveries accepted, waiting for rating

	   // Final States
	   completed,     // Order successfully finished (Rating done or skipped)
	   cancelled      // Order cancelled at some point
}
