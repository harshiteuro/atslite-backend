package com.harshit.tldr;

import com.harshit.tldr.repository.UserInfoRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = UserInfoRepo.class)
public class TldrApplication {

	public static void main(String[] args) {
		SpringApplication.run(TldrApplication.class, args);
	}

}
