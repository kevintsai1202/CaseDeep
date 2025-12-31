package com.casemgr.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank; // For validation
// Consider adding validation for file uploads if applicable
// import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModificationRequested implements Serializable {

    // orderId is usually passed in the path or set by the service
    
    private String comments;

    // How to handle file uploads?
    // Option 1: Include MultipartFile directly (Controller needs @RequestPart)
    // private MultipartFile file;

    // Option 2: Upload file separately, then provide URL/ID here
//    private String fileUrl; // URL or ID of the uploaded file

    // Other fields? e.g., specific milestone identifier?
}