package com.casemgr.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casemgr.entity.Evaluate;
import com.casemgr.entity.Order;
import com.casemgr.entity.User;
import com.casemgr.enumtype.EvaluateItem;
import com.casemgr.enumtype.EvaluateType;

public interface EvaluateRepository extends JpaRepository<Evaluate, Long> {
	
	List<Evaluate> findByOrderAndEvaluatee(Order order, User user);
	List<Evaluate> findByEvaluatee(User evaluatee);
//	List<Evaluate> findByEvaluateeAndType(User evaluatee, Integer type);
	List<Evaluate> findByEvaluateeAndTypeAndItem(User evaluatee, EvaluateType type, Integer item);
}
