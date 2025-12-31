package com.casemgr.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casemgr.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
