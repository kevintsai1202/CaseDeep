package com.casemgr.request;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BlockUpdateRequest implements Serializable {

    private Long blockId; // The ID of the Block being updated

    // For BlockType.list (Option blocks)
    private List<Long> selectedListItemIds; // List of IDs for the selected ListItems

    // For BlockType.text (Input blocks)
    private String context; // The updated text content

    // Add other fields if necessary, e.g., quantity for a list item if applicable
}