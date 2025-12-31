package com.casemgr.request;

import java.io.Serializable;

import jakarta.validation.constraints.Max; // For validation
import jakarta.validation.constraints.Min; // For validation
import jakarta.validation.constraints.NotNull; // For validation
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RatingRequest implements Serializable {

    // Assuming 3 rating items as per Evaluate entity (item 0, 1, 2)
    // Adjust based on actual requirements

    @NotNull(message = "Rating for item 1 cannot be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 10, message = "Rating must be at most 10") // Or 5 if it's a 5-star system
    private Integer ratingItem1; // Rating for item 0

    @NotNull(message = "Rating for item 2 cannot be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 10, message = "Rating must be at most 10")
    private Integer ratingItem2; // Rating for item 1

    @NotNull(message = "Rating for item 3 cannot be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 10, message = "Rating must be at most 10")
    private Integer ratingItem3; // Rating for item 2

    private String comment; // Optional overall comment

}