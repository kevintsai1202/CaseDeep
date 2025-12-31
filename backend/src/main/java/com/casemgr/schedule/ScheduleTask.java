package com.casemgr.schedule;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.casemgr.entity.User;
import com.casemgr.repository.UserRepository;
import com.casemgr.service.impl.MailServiceImpl;
import com.resend.core.exception.ResendException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ScheduleTask {

	@Autowired
	private MailServiceImpl mailService;

	@Autowired
	private UserRepository userRepository;

	@Scheduled(fixedRate = 100000)
	public void checkAndSendEmails() {
//		processApproveActions();
		verifyCodeSender();
		resetCodeSender();
	}

	private void verifyCodeSender() {
		log.info("Begin Send Verify mail");
		List<User> users = userRepository.findAllBySendVerifyFlag(false);
		for (User user : users) {
			log.info("Send verify mail to:{}", user.getUsername());
			String code = user.getVerifyCode();
			String subject = String.format("Your CodeTest Verification Code: [%s]", code);
			String context = String
					.format("Dear [%s]\r\n" + "Thank you for your request! Your CodeTest verification code is:\r\n"
							+ "*[%s]*\r\n" + "Please enter this code to complete your verification.\r\n"
							+ "If you did not create an account, please ignore this email.\r\n" + "\r\n"
							+ "Thank you, \r\n" + "CodeTest Team", user.getUsername(), code);

			try {
				mailService.sendEmail(user.getEmail(), subject, context);
			} catch (ResendException e) {
				// TODO Auto-generated catch block
				log.error("Send Valid Mail Error", e);
			}
			user.setSendVerifyFlag(true);
			userRepository.save(user);
//	        SimpleMailMessage message = new SimpleMailMessage();
//	        log.info("Email:{}",user.getEmail());
//	        message.setFrom("support@casedeep.com");
//	        message.setTo(user.getEmail());
//	        message.setSubject( String.format("Your CodeTest Verification Code: [%s]", code));
//	        message.setText(context);
//	        mailSender.send(message);
		}

	}

	private void resetCodeSender() {
		log.info("Begin Send Reset mail");
		List<User> users = userRepository.findAllBySendResetFlag(false);
		String subject = "Password Reset Request";
		for (User user : users) {
			log.info("Send reset mail to:{}", user.getUsername());
			String resetUrl = "http://162.43.92.30:8080/resetpassword?resetcode=" + user.getResetCode();
			String context = String.format("Dear [%s]\r\n"
					+ "We received a request to reset your password. Please click the link below to reset your password:\r\n"
					+ "%s" + "\r\n"
					+ "Please note that this link is valid for 60 seconds only. If you do not reset your password within this time frame, you will need to request a new password reset link.\r\n"
					+ "If you did not request this change, please ignore this email, and your password will remain unchanged.\r\n"
					+ "\r\n" + "Thank you!\r\n" + "CodeTest Team", user.getUsername(), resetUrl);
			try {
				mailService.sendEmail(user.getEmail(), subject, context);
			} catch (ResendException e) {
				// TODO Auto-generated catch block
				log.error("Send Reset Code Mail Error", e);
			}
			
			user.setSendResetFlag(true);
			userRepository.save(user);
		}
//		SimpleMailMessage message = new SimpleMailMessage();
//		message.setTo(user.getEmail());
//		message.setFrom("support@casedeep.com");
//		message.setSubject("Password Reset Request");
//		message.setText(context);
//		mailSender.send(message);
	}

}
