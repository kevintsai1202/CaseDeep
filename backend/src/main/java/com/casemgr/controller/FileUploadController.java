package com.casemgr.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.casemgr.entity.Filedata;
import com.casemgr.repository.FiledataRepository;
import com.casemgr.response.FiledataResponse;
import com.casemgr.service.FileStorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "File Management", description = "APIs for file upload, download, and management operations including document storage and retrieval")
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@CrossOrigin
public class FileUploadController {
	private final FileStorageService fileStorageService;
	private final FiledataRepository filedataRepository;
//	public FileUploadController(FileStorageService fileStorageService, FiledataRepository filedataRepository) {
//		this.fileStorageService = fileStorageService;
//		this.filedataRepository = filedataRepository;
//	}

	/**
	 * Upload a file to the server.
	 * @param file The multipart file to upload
	 * @return File upload response with metadata
	 */
	@PostMapping(consumes = "multipart/*" , headers = "content-type=multipart/form-data")
	@Operation(
		summary = "Upload File",
		description = "Upload a file to the server storage system. The file will be stored securely with a unique identifier " +
					  "and metadata including original filename, size, and storage UUID for future retrieval."
	)
	public ResponseEntity<FiledataResponse> upload(
		@Parameter(description = "Multipart file to upload", required = true)
		@RequestParam("file") MultipartFile file) {
		return ResponseEntity.ok(new FiledataResponse(fileStorageService.save(file)));
	}
//	public ResponseEntity<MessageResponse> upload(@RequestParam("file") MultipartFile file) {
//		try {
//			fileStorageService.save(file);
//			return ResponseEntity.ok(new MessageResponse("Upload file successfully: " + file.getOriginalFilename()));
//		} catch (Exception e) {
//			return ResponseEntity.badRequest()
//					.body(new MessageResponse("Could not upload the file:" + file.getOriginalFilename()));
//		}
//	}

	/**
	 * Get a list of all uploaded files.
	 * @return List of all files with their metadata
	 */
	@GetMapping
	@Operation(
		summary = "List All Files",
		description = "Retrieve a comprehensive list of all uploaded files with their metadata including filenames, " +
					  "storage UUIDs, file sizes, and download URLs. This endpoint provides an overview of all files in the system."
	)
	   public ResponseEntity<List<FiledataResponse>> files(){
		String homeURL = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
		List<Filedata> filedatas= filedataRepository.findAll();
		List<FiledataResponse> files = new ArrayList<>();
				
		filedatas.forEach(filedata -> {
			files.add(new FiledataResponse(filedata.getFId(), filedata.getFileName(),filedata.getStorageUuid(), filedata.getSize(), homeURL+"/api/files/"+filedata.getStorageUuid())); 
		});
		
//        List<FiledataResponse> files = fileStorageService.load()  
//                .map(path -> {  
//                    String fileName = path.getFileName().toString();  
//                    String url = MvcUriComponentsBuilder  
//                            .fromMethodName(FileUploadController.class,  
//                                    "getFile",  
//                                    path.getFileName().toString()  
//                            ).build().toString();  
//                    return new FiledataResponse(fileName,"1234", 123L,url);
//                }).collect(Collectors.toList());  
        return ResponseEntity.ok(files);  
    }  
  
//    @GetMapping("/{filename:.+}") 
//    public ResponseEntity<Resource> getFile(@PathVariable("filename")String filename){  
//        Resource file = fileStorageService.load(filename);  
//        return ResponseEntity.ok()  
//                .header(HttpHeaders.CONTENT_DISPOSITION,  
//                        "attachment;filename=\""+file.getFilename()+"\"")  
//                .body(file);  
//    }
    
    /**
     * Download a file by its UUID.
     * @param uuid The unique identifier of the file to download
     * @return File resource for download
     */
    @GetMapping("/{uuid}")
    @Operation(
  summary = "Download File by UUID",
  description = "Download a specific file using its unique UUID identifier. The file will be returned as an attachment " +
    	  "with the original filename and appropriate content headers for browser download."
 )
    public ResponseEntity<Resource> getFile(
  @Parameter(description = "Unique UUID identifier of the file to download", required = true)
  @PathVariable("uuid") String uuid){
        Resource file = fileStorageService.load(uuid);  
        Filedata fd = filedataRepository.findByStorageUuid(uuid);
        return ResponseEntity.ok()  
                .header(HttpHeaders.CONTENT_DISPOSITION,  
                        "attachment;filename=\""+fd.getFileName()+"\"")  
                .body(file);  
    }  
}
