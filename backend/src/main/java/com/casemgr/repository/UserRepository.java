package com.casemgr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.casemgr.entity.User;
import com.casemgr.enumtype.StatusType;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
	public Optional<User> findByUsername(String username);
	
	@Modifying
	@Query(value = "UPDATE t_user SET STATUS = :status WHERE STATUS != :status" ,nativeQuery = true)
	public void updateAllUserStatus(int status);
	
	public List<User> findAllByStatus(StatusType status);
	
	
	public List<User> findAllBySendVerifyFlag(Boolean flag);
	public List<User> findAllBySendResetFlag(Boolean flag);
	public User findByResetCode(String resetCode);
	
	public User findByEmail(String email);
	
	 // Methods needed for Admin User Management
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRegion(String region);
    Page<User> findByRegion(String region, Pageable pageable); // Added version with Pageable
    boolean existsByInvitationCode(String invitationCode);
    
    // Methods for display order management
    Optional<User> findTopByDisplayOrderLessThanOrderByDisplayOrderDesc(Integer displayOrder);
    Optional<User> findTopByDisplayOrderGreaterThanOrderByDisplayOrderAsc(Integer displayOrder);
    List<User> findAllByOrderByDisplayOrderAsc();
    
    @Query("SELECT MAX(u.displayOrder) FROM User u")
    Optional<Integer> findMaxDisplayOrder();
}
