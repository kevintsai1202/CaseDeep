package com.casemgr.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.casemgr.entity.Certification; // Assuming needed
import com.casemgr.entity.Filedata; // Assuming needed
import com.casemgr.enumtype.CEStatus; // Assuming needed
import com.casemgr.exception.BusinessException; // Assuming needed
import com.casemgr.response.CertificationResponse;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException; // Added import

public interface CertificationService {

    // Methods likely existing in CertificationServiceImpl (based on its content)
    CertificationResponse updateCertification(Certification certification);
    CertificationResponse getCertification(String ceNoBase62);
    CertificationResponse createCE() throws EntityExistsException;
    CertificationResponse uploadDeal(String ceNoBase62, Filedata fileData) throws BusinessException;
    CertificationResponse uploadIdentity(String ceNoBase62, Filedata fileData) throws BusinessException;
    CertificationResponse submitCE(String ceNoBase62) throws BusinessException;
    CertificationResponse acceptCE(String ceNoBase62) throws BusinessException; // Might be admin only?
    CertificationResponse approveCE(String ceNoBase62, Double score) throws BusinessException; // Might be admin only?
    CertificationResponse rejectCE(String ceNoBase62, String comment) throws BusinessException; // Might be admin only?
    CertificationResponse inputFeeCode(String ceNoBase62, String code) throws BusinessException;
    CertificationResponse payFee(String ceNoBase62, Double amount) throws BusinessException;
    CertificationResponse signCE(String ceNoBase62, String signName) throws BusinessException;
    Double setCommissionRate(String ceNoBase62, Double rate) throws BusinessException;
    List<CertificationResponse> listSubmitCertification(); // Might be admin only?
    List<CertificationResponse> listAllCertification(); // Likely admin only
    List<CertificationResponse> listMyCertification();

    // --- New methods for Admin ---

    /**
     * Lists certification records for administrators, potentially filtered by region.
     * @param pageable Pagination and sorting information.
     * @return A page of CertificationResponse objects.
     */
    Page<CertificationResponse> listCertificationsForAdmin(Pageable pageable);

    /**
     * Updates the status and potentially other details of a certification by an administrator.
     * @param certificationId The ID of the certification record.
     * @param newStatus The new status.
     * @param reviewComment Optional review comment.
     * @param score Optional score assigned by admin.
     * @return The updated CertificationResponse.
     * @throws EntityNotFoundException if the certification record is not found.
     * @throws BusinessException for other business rule violations.
     */
    CertificationResponse updateCertificationByAdmin(Long certificationId, CEStatus newStatus, String reviewComment, Double score) throws EntityNotFoundException, BusinessException;

}