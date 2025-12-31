package com.casemgr.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.casemgr.entity.Filedata;
import com.casemgr.exception.BusinessException;
import com.casemgr.response.CertificationResponse;
import com.casemgr.service.FileStorageService;
import com.casemgr.service.impl.CertificationServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Certification Management", description = "APIs for managing user certifications, document uploads, approval processes, and certification lifecycle")
@RestController
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
@CrossOrigin
public class CertificationController {
	
	private final CertificationServiceImpl certificationService;
	private final FileStorageService fileStorageService;
	
	@PostMapping
	@Operation(
		summary = "Create Certification Application",
		description = "Create a new certification application for the current user. " +
					  "This initiates the certification process and generates a unique certification number for tracking."
	)
	public ResponseEntity<CertificationResponse> apply() {
		return ResponseEntity.ok(certificationService.createCE());
	}
	
	@GetMapping("/{cenobase62}")
	@Operation(
		summary = "Get Certification by Number",
		description = "Retrieve detailed information about a specific certification using its base62-encoded certification number. " +
					  "This provides complete certification status, documents, and processing history."
	)
	public ResponseEntity<CertificationResponse> getCertification(
		@Parameter(description = "Base62-encoded certification number", required = true)
		@PathVariable("cenobase62") String cenobase62) {
		return ResponseEntity.ok(certificationService.getCertification(cenobase62));
	}
	
	
	@GetMapping("/me")
	@Operation(
		summary = "List My Certifications",
		description = "Retrieve all certification applications submitted by the current user. " +
					  "This provides a complete history of the user's certification requests and their current status."
	)
	public ResponseEntity<List<CertificationResponse>> listMyCertification() {
		List<CertificationResponse> certs;
		certs = certificationService.listMyCertification();
		return ResponseEntity.ok(certs);
	}
	
	@GetMapping("/all")
	@Secured("ROLE_ADMIN")
	@Operation(
		summary = "List All Certifications (Admin)",
		description = "Retrieve all certification applications in the system. " +
					  "This administrative endpoint provides complete visibility into all certification requests for management and oversight."
	)
	public ResponseEntity<List<CertificationResponse>> listAllCertification() {
		List<CertificationResponse> certs;
		certs = certificationService.listAllCertification();
		return ResponseEntity.ok(certs);
	}
	
	@GetMapping("/status/submit")
	@Secured("ROLE_ADMIN")
	@Operation(
		summary = "List Submitted Certifications (Admin)",
		description = "Retrieve all certification applications that have been submitted and are pending review. " +
					  "This administrative endpoint helps manage the certification approval workflow."
	)
	public ResponseEntity<List<CertificationResponse>> listSubmitCertification() {
		return ResponseEntity.ok(certificationService.listSubmitCertification());
	}
	
	
	@PatchMapping(value="/{cenobase62}/uploaddeal", consumes = "multipart/*" , headers = "content-type=multipart/form-data")
	@Operation(
		summary = "Upload Deal Document",
		description = "Upload deal-related documentation for a certification application. " +
					  "This endpoint accepts multipart file uploads for business transaction records and related documents."
	)
	public ResponseEntity<CertificationResponse> uploadDeal(
		@Parameter(description = "Base62-encoded certification number", required = true)
		@PathVariable("cenobase62") String ceNoBase62,
		@Parameter(description = "Deal document file to upload", required = true)
		@RequestParam("file") MultipartFile file) throws BusinessException {
		Filedata fileData = fileStorageService.save(file);
		return ResponseEntity.ok( certificationService.uploadDeal(ceNoBase62, fileData));
	}
	
	@PatchMapping(value="/{cenobase62}/uploadidentity", consumes = "multipart/*" , headers = "content-type=multipart/form-data")
	@Operation(
		summary = "Upload Identity Document",
		description = "Upload identity verification documents for a certification application. " +
					  "This endpoint accepts multipart file uploads for ID cards, passports, or other identity verification materials."
	)
	public ResponseEntity<CertificationResponse> uploadFile(
		@Parameter(description = "Base62-encoded certification number", required = true)
		@PathVariable("cenobase62") String ceNoBase62,
		@Parameter(description = "Identity document file to upload", required = true)
		@RequestParam("file") MultipartFile file) throws BusinessException {
		Filedata fileData = fileStorageService.save(file);
		return ResponseEntity.ok( certificationService.uploadIdentity(ceNoBase62, fileData));
	}
	
	@PatchMapping("/{cenobase62}/submit")
	@Operation(
		summary = "Submit Certification Application",
		description = "Submit a certification application for review after all required documents have been uploaded. " +
					  "This moves the certification from draft status to submitted status for administrative review."
	)
	public ResponseEntity<CertificationResponse> submit(
		@Parameter(description = "Base62-encoded certification number", required = true)
		@PathVariable("cenobase62") String ceNoBase62) throws BusinessException {
		return ResponseEntity.ok(certificationService.submitCE(ceNoBase62));
	}
	
	@PatchMapping("/{cenobase62}/signname/{signname}")
	@Operation(
		summary = "Sign Certification Application",
		description = "Add digital signature to a certification application using the applicant's name. " +
					  "This step is required to authenticate and validate the certification request."
	)
	public ResponseEntity<CertificationResponse> signName(
		@Parameter(description = "Base62-encoded certification number", required = true)
		@PathVariable("cenobase62") String ceNoBase62,
		@Parameter(description = "Digital signature name for the certification", required = true)
		@PathVariable("signname") String signName) throws BusinessException {
		return ResponseEntity.ok(certificationService.signCE(ceNoBase62, signName));
	}
	
	@PatchMapping("/{cenobase62}/feecode/{code}")
	@Operation(
		summary = "Input Fee Code",
		description = "Enter a fee code for certification payment processing. " +
					  "This code is used to calculate and apply appropriate certification fees based on the service type."
	)
	public ResponseEntity<CertificationResponse> inputFeeCode(
		@Parameter(description = "Base62-encoded certification number", required = true)
		@PathVariable("cenobase62") String ceNoBase62,
		@Parameter(description = "Fee code for certification payment calculation", required = true)
		@PathVariable("code") String code) throws BusinessException {
		return ResponseEntity.ok(certificationService.inputFeeCode(ceNoBase62, code));
	}
	
	@PatchMapping("/{cenobase62}/payment/{amount}")
	@Operation(
		summary = "Process Certification Payment",
		description = "Process payment for certification services with the specified amount. " +
					  "This endpoint handles the financial transaction for certification processing fees."
	)
	public ResponseEntity<CertificationResponse> fee(
		@Parameter(description = "Base62-encoded certification number", required = true)
		@PathVariable("cenobase62") String ceNoBase62,
		@Parameter(description = "Payment amount for certification services", required = true)
		@PathVariable("amount") Double amount) throws BusinessException {
		return ResponseEntity.ok(certificationService.payFee(ceNoBase62, amount));
	}
	
	@PatchMapping("/{cenobase62}/commissionrate/{rate}")
	@Operation(
		summary = "Set Commission Rate",
		description = "Set the commission rate for a certification application. " +
					  "This determines the percentage of commission that will be applied to transactions related to this certification."
	)
	public ResponseEntity<Double> setCommissionRate(
		@Parameter(description = "Base62-encoded certification number", required = true)
		@PathVariable("cenobase62") String ceNoBase62,
		@Parameter(description = "Commission rate percentage (0.0 to 1.0)", required = true)
		@PathVariable("rate") Double rate) throws BusinessException {
		return ResponseEntity.ok(certificationService.setCommissionRate(ceNoBase62, rate));
	}
	
//	@PatchMapping("/{cenobase62}/accept")
//	@Operation(summary = "Accept Certification", description = "Accept Certification")
//	public ResponseEntity<CertificationResponse> accept(@PathVariable("cenobase62") String ceNoBase62) throws BusinessException {
//		return ResponseEntity.ok(certificationService.acceptCE(ceNoBase62));
//	}
	
	@PatchMapping("/{cenobase62}/approve/{score}")
	@Secured("ROLE_ADMIN")
	@Operation(
		summary = "Approve Certification (Admin)",
		description = "Approve a certification application with a given score. " +
					  "This administrative action completes the certification review process and grants certification status to the applicant."
	)
	public ResponseEntity<CertificationResponse> approve(
		@Parameter(description = "Base62-encoded certification number", required = true)
		@PathVariable("cenobase62") String ceNoBase62,
		@Parameter(description = "Certification score (0.0 to 100.0)", required = true)
		@PathVariable("score") Double score) throws BusinessException {
		return ResponseEntity.ok(certificationService.approveCE(ceNoBase62, score));
	}
	
	@PatchMapping("/{cenobase62}/reject/{comment}")
	@Secured("ROLE_ADMIN")
	@Operation(
		summary = "Reject Certification (Admin)",
		description = "Reject a certification application with a reason comment. " +
					  "This administrative action denies the certification request and provides feedback to the applicant."
	)
	public ResponseEntity<CertificationResponse> reject(
		@Parameter(description = "Base62-encoded certification number", required = true)
		@PathVariable("cenobase62") String ceNoBase62,
		@Parameter(description = "Reason for certification rejection", required = true)
		@PathVariable("comment") String comment) throws BusinessException {
		return ResponseEntity.ok( certificationService.rejectCE(ceNoBase62, comment));
	}
}
