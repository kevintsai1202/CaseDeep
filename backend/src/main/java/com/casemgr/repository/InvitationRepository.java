package com.casemgr.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.casemgr.entity.Invitation;
import com.casemgr.entity.User;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    // You can add custom query methods here if needed, e.g.:
    List<Invitation> findByInvitingUser(User invitingUser);
    List<Invitation> findByInvitedUser(User invitedUser);
    Optional<Invitation> findByInvitationIdStr(String invitationIdStr);
    Optional<Invitation> findByInvitedUserAndInvitationCode(User invitedUser, String invitationCode);
    Page<Invitation> findByInvitingUser_RegionOrInvitedUser_Region(String invitingRegion, String invitedRegion, Pageable pageable);
}