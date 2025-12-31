package com.casemgr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casemgr.entity.Message;

public interface ChatMessageRepository extends JpaRepository<Message, Long> {

	List<Message> findByChatCode(String chatCode);
	
}
