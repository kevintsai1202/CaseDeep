package com.casemgr.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casemgr.entity.Evaluate;
import com.casemgr.entity.Order;
import com.casemgr.entity.User;
import com.casemgr.enumtype.EvaluateItem;
import com.casemgr.enumtype.EvaluateType;
import com.casemgr.exception.BusinessException;
import com.casemgr.repository.EvaluateRepository;
import com.casemgr.repository.OrderRepository;
import com.casemgr.repository.UserRepository;
import com.casemgr.response.ClientEvaluateSummary;
import com.casemgr.response.ProviderEvaluateSummary;
import com.casemgr.service.EvaluateService;
import com.casemgr.utils.Base62Utils;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class EvaluateServiceImpl implements EvaluateService {

	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	EvaluateRepository evaluateRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserServiceImpl userService;
	
	@Transactional
	@Override
	public 	Evaluate setClientEvaluate(Long oId, Integer item, Integer rating, String comment) throws EntityNotFoundException,EntityExistsException, BusinessException {
		Order order = orderRepository.getReferenceById(oId);
		if (order == null) {
			throw new EntityNotFoundException("Order not found");
		}
		Optional<Evaluate> opClientEvaluate = order.getEvaluates().stream().filter(evaluate -> evaluate.getType().equals(EvaluateType.Client) && evaluate.getItem().equals(item)).findFirst();
		if ( opClientEvaluate.isPresent() ) {
			throw new EntityExistsException("Already rated");
		}
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		UserDetails me =userService.loadUserByUsername(authentication.getName());
		if (!order.getProvider().equals(me))
			throw new BusinessException("You are not provider");
		
		Evaluate newEvaluate = Evaluate.builder()
								.order(order)
								.evaluatee(order.getClient())
								.evaluator((User) me)
								.item(item)
								.rating(rating)
								.comment(comment)
								.type(EvaluateType.Client)
								.build();
		Evaluate savedEvaluate = evaluateRepository.save(newEvaluate);
//		project.setClientEvaluate(savedEvaluate);
//		projectRepository.save(project);
		return savedEvaluate;
	}

	@Transactional
	@Override
	public 	Evaluate setProviderEvaluate(Long oId, Integer item, Integer rating, String comment) throws EntityNotFoundException,EntityExistsException, BusinessException {
		Order order = orderRepository.getReferenceById(oId);
		if (order == null) {
			throw new EntityNotFoundException("Project not found");
		}
		Optional<Evaluate> opProviderEvaluate = order.getEvaluates().stream().filter(evaluate -> evaluate.getType().equals(EvaluateType.Provider) && evaluate.getItem().equals(item)).findFirst();
		if ( opProviderEvaluate.isPresent() ) {
			throw new EntityExistsException("Already rated");
		}
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		UserDetails me =userService.loadUserByUsername(authentication.getName());
		System.out.println("project.getClient(): "+order.getClient());
		System.out.println("authentication.getName():"+authentication.getName());
		System.out.println("me:"+me);
		if (!order.getClient().equals(me))
			throw new BusinessException("You are not client");
		
		Evaluate newEvaluate = Evaluate.builder()
								.order(order)
								.evaluatee(order.getProvider())
								.evaluator((User) me)
								.item(item)
								.rating(rating)
								.comment(comment)
								.type(EvaluateType.Provider)
								.build();
		Evaluate savedEvaluate = evaluateRepository.save(newEvaluate);
//		project.setProviderEvaluate(savedEvaluate);
//		projectRepository.save(project);
		return savedEvaluate;
	}
	
	@Transactional
	@Override
	public Evaluate getClientEvaluateByOrder(Long oId, Integer item) throws EntityNotFoundException {
		Order order = orderRepository.getReferenceById(oId);
		if (order == null) throw new EntityNotFoundException("Project not found");
		
		Optional<Evaluate> opClientEvaluate = order.getEvaluates().stream().filter(evaluate -> evaluate.getType().equals(EvaluateType.Client.value()) && evaluate.getItem().equals(item)).findFirst();
		if (opClientEvaluate.isPresent()) {
			return opClientEvaluate.get();
		}else {
			return null;
		}
	}

	@Transactional
	@Override
	public Evaluate getProviderEvaluateByOrder(Long oId, Integer item) throws EntityNotFoundException {
		Order order = orderRepository.getReferenceById(oId);
		if (order == null) throw new EntityNotFoundException("Project not found");

		Optional<Evaluate> opProviderEvaluate = order.getEvaluates().stream().filter(evaluate -> evaluate.getType().equals(EvaluateType.Provider.value()) && evaluate.getItem().equals(item)).findFirst();
		if (opProviderEvaluate.isPresent()) {
			return opProviderEvaluate.get();
		}else {
			return null;
		}
	}
	
	@Transactional
	@Override
	public List<Evaluate> getClientEvaluatesByUser(Long uId, Integer item) throws EntityNotFoundException {
		User user = userRepository.getReferenceById(uId);
		return evaluateRepository.findByEvaluateeAndTypeAndItem(user,  EvaluateType.Client, item );
	}
	
	@Transactional
	@Override
	public List<Evaluate> getProviderEvaluatesByUser(Long uId, Integer item) throws EntityNotFoundException {
		User user = userRepository.getReferenceById(uId);
		return evaluateRepository.findByEvaluateeAndTypeAndItem(user, EvaluateType.Provider, item);
	}

	@Transactional
	@Override
	public Evaluate updateEvaluate(Long eId, Integer rating, String comment) throws EntityNotFoundException, BusinessException {
		Evaluate evaluate = evaluateRepository.getReferenceById(eId);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		UserDetails me =userService.loadUserByUsername(authentication.getName());
		
		if (!evaluate.getEvaluator().equals(me))
			throw new BusinessException("You are not evaluator");
		
		evaluate.setRating(rating);
		evaluate.setComment(comment);
		return evaluateRepository.save(evaluate);
	}

	@Override
	public ClientEvaluateSummary getClientEvaluateSummaryByUser(Long uId) throws EntityNotFoundException {
		List<Evaluate> item1Evaluates = getClientEvaluatesByUser(uId, EvaluateItem.Item1.value());
		List<Evaluate> item2Evaluates = getClientEvaluatesByUser(uId, EvaluateItem.Item2.value());
		List<Evaluate> item3Evaluates = getClientEvaluatesByUser(uId, EvaluateItem.Item3.value());
		Double item1RateAvg = item1Evaluates.stream().collect(Collectors.averagingInt(Evaluate::getRating));
		Double item2RateAvg = item2Evaluates.stream().collect(Collectors.averagingInt(Evaluate::getRating));
		Double item3RateAvg = item3Evaluates.stream().collect(Collectors.averagingInt(Evaluate::getRating));
		ClientEvaluateSummary ces = ClientEvaluateSummary.builder()
			.completeRate(0.9) //結案率
			.recoveryRate(0.5) //復案率
			.proposalRate(0.3) //提案率
			.responsibility(item1RateAvg) //責任心
			.principled(item2RateAvg) //原則性
			.assistance(item3RateAvg) //協助力
			.honour(50)//榮譽
			.referendum(10)//公投
			.recommend(100)//推薦
		.build();
		return ces;
	}

	@Override
	public ProviderEvaluateSummary getProviderEvaluateSummaryByUser(Long uId) throws EntityNotFoundException {
		List<Evaluate> item1Evaluates = getProviderEvaluatesByUser(uId, EvaluateItem.Item1.value());
		List<Evaluate> item2Evaluates = getProviderEvaluatesByUser(uId, EvaluateItem.Item2.value());
		List<Evaluate> item3Evaluates = getProviderEvaluatesByUser(uId, EvaluateItem.Item3.value());
		Double item1RateAvg = item1Evaluates.stream().collect(Collectors.averagingInt(Evaluate::getRating));
		Double item2RateAvg = item2Evaluates.stream().collect(Collectors.averagingInt(Evaluate::getRating));
		Double item3RateAvg = item3Evaluates.stream().collect(Collectors.averagingInt(Evaluate::getRating));
		ProviderEvaluateSummary pes = ProviderEvaluateSummary.builder()
			.completeRate(0.9) //結案率
			.recoveryRate(0.5) //復案率
			.acceptanceRate(0.3) //接案率
			.responsibility(item1RateAvg) //責任心
			.professionalism(item2RateAvg) //專業性
			.cooperativeness(item3RateAvg) //配合度
			.honour(50)//榮譽
			.referendum(10)//公投
			.recommend(100)//推薦
		.build();
		
		
		return pes;
	}

	@Override
	public Evaluate setClientEvaluate(String base62no, Integer item, Integer rating, String comment)
			throws EntityNotFoundException, EntityExistsException, BusinessException {
		String orderNo = Base62Utils.decode(base62no);
		Order order = orderRepository.getByOrderNo(orderNo)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with order no: " + orderNo));
		return setClientEvaluate(order.getOId(), item, rating, comment);
	}

	@Override
	public Evaluate setProviderEvaluate(String base62no, Integer item, Integer rating, String comment)
			throws EntityNotFoundException, EntityExistsException, BusinessException {
		String orderNo = Base62Utils.decode(base62no);
		Order order = orderRepository.getByOrderNo(orderNo)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with order no: " + orderNo));
		return setProviderEvaluate(order.getOId(), item, rating, comment);
	}

	@Override
	public Evaluate getClientEvaluateByOrder(String base62no, Integer item) throws EntityNotFoundException {
		String orderNo = Base62Utils.decode(base62no);
		Order order = orderRepository.getByOrderNo(orderNo)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with order no: " + orderNo));
		return getClientEvaluateByOrder(order.getOId(), item);
	}

	@Override
	public Evaluate getProviderEvaluateByOrder(String base62no, Integer item) throws EntityNotFoundException {
		String orderNo = Base62Utils.decode(base62no);
		Order order = orderRepository.getByOrderNo(orderNo)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with order no: " + orderNo));
		return getProviderEvaluateByOrder(order.getOId(), item);
	}

	@Override
	public List<Evaluate> getClientEvaluateByOrder(String base62no) throws EntityNotFoundException {
		String orderNo = Base62Utils.decode(base62no);
		Order order = orderRepository.getByOrderNo(orderNo)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with order no: " + orderNo));
		
		return evaluateRepository.findByOrderAndEvaluatee(order, order.getClient());
	}

	@Override
	public List<Evaluate> getProviderEvaluateByOrder(String base62no) throws EntityNotFoundException {
		String orderNo = Base62Utils.decode(base62no);
		Order order = orderRepository.getByOrderNo(orderNo)
				.orElseThrow(() -> new EntityNotFoundException("Order not found with order no: " + orderNo));
		return evaluateRepository.findByOrderAndEvaluatee(order, order.getProvider());
	}

}
