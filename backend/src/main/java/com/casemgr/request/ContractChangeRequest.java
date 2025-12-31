package com.casemgr.request;

import java.io.Serializable;
import java.util.List;

// Assuming BlockUpdateRequest exists or similar structure for changes
// import com.casemgr.request.BlockUpdateRequest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContractChangeRequest implements Serializable {

    // private Long contractId; // Optional, often passed as path variable

    private String changeReason; // Reason for requesting the change

    // How to represent changes? Could be a list of block updates,
    // or specific fields for term changes. Using BlockUpdateRequest for now.
    // private List<BlockUpdateRequest> blockChanges;

    // Or maybe just text fields for proposed changes?
    private String proposedChangesText;

}