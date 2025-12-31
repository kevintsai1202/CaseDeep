package com.casemgr.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.casemgr.entity.Certification;
import com.casemgr.entity.User;
import com.casemgr.enumtype.CEStatus;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

	public List<Certification> findByApplicantAndStatusNot(User applicant, CEStatus status);
	
	public List<Certification> findByAuditorAndStatus(User auditor, CEStatus status);
	
	public List<Certification> findByApplicant(User applicant);
	
	public List<Certification> findByStatus(CEStatus status);
	
	public Certification findByCeNo(String ceNo);
	

    // Find certifications by applicant's region for admin filtering
    Page<Certification> findByApplicant_Region(String region, Pageable pageable);
}
