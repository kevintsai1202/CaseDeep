package com.casemgr.request;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BlockSelectRequest implements Serializable {
	// For BlockType.list (Option blocks)
    private List<Long> selectedListItemIds; // List of IDs for the selected ListItems
}