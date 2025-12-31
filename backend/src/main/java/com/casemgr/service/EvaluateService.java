package com.casemgr.service;

import java.util.List;

import com.casemgr.entity.Evaluate;
import com.casemgr.enumtype.EvaluateItem;
import com.casemgr.enumtype.EvaluateType;
import com.casemgr.exception.BusinessException;
import com.casemgr.request.ContractRequest;
import com.casemgr.response.ContractResponse;
import com.casemgr.response.ProviderEvaluateSummary;
import com.casemgr.response.ClientEvaluateSummary;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

public interface EvaluateService {
	
	//提案方給評價，需確認專案已完成
	Evaluate setClientEvaluate(String base62no, Integer item, Integer rating, String comment) throws EntityNotFoundException, EntityExistsException, BusinessException;
	//提案方給評價，需確認專案已完成
	Evaluate setClientEvaluate(Long oId, Integer item, Integer rating, String comment) throws EntityNotFoundException, EntityExistsException, BusinessException;
	
	//項目方給評價，需確認專案已完成
	Evaluate setProviderEvaluate(String base62no, Integer item, Integer rating, String comment) throws EntityNotFoundException, EntityExistsException, BusinessException;
	//項目方給評價，需確認專案已完成
	Evaluate setProviderEvaluate(Long oId, Integer item, Integer rating, String comment) throws EntityNotFoundException, EntityExistsException, BusinessException;
	
	//從專案取得提案方評價
	Evaluate getClientEvaluateByOrder(String base62no, Integer item) throws EntityNotFoundException;
	
	//從專案取得提案方評價
	List<Evaluate> getClientEvaluateByOrder(String base62no) throws EntityNotFoundException;
	
	//從專案取得提案方評價
	Evaluate getClientEvaluateByOrder(Long oId, Integer item) throws EntityNotFoundException;
	
	//從專案取得項目方評價
	Evaluate getProviderEvaluateByOrder(String base62no, Integer item) throws EntityNotFoundException;
	//從專案取得項目方評價
	Evaluate getProviderEvaluateByOrder(Long oId, Integer item) throws EntityNotFoundException;
	//從專案取得項目方評價
	List<Evaluate> getProviderEvaluateByOrder(String base62no) throws EntityNotFoundException;
		
	//取得帳號提案方綜合評價
	ClientEvaluateSummary getClientEvaluateSummaryByUser(Long uId) throws EntityNotFoundException;
	
	//取得帳號接案方綜合評價
	ProviderEvaluateSummary getProviderEvaluateSummaryByUser(Long uId) throws EntityNotFoundException;
	
	//更改評價,需判斷是自己給的評價才能修改
	Evaluate updateEvaluate(Long eId, Integer rating, String comment) throws EntityNotFoundException, BusinessException;

	List<Evaluate> getClientEvaluatesByUser(Long uId, Integer item) throws EntityNotFoundException;

	List<Evaluate> getProviderEvaluatesByUser(Long uId, Integer item) throws EntityNotFoundException;
}
