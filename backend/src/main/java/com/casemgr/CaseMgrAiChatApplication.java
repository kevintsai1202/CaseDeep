package com.casemgr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.casemgr.service.FileStorageService;
import com.casemgr.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(info = @Info(title = "Case Deep API", version = "0.8.5", description = "Case Deep API"))
public class CaseMgrAiChatApplication implements CommandLineRunner{

	@Autowired
	private FileStorageService fileStorageService;
	
	@Autowired
	private UserServiceImpl userService;
	
	public static void main(String[] args) {
		SpringApplication.run(CaseMgrAiChatApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Runner Case Deep!!");
		fileStorageService.init();
		userService.initAllUserStatus();
	}

}
