package com.jiho.board.springbootaws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@EnableAspectJAutoProxy
public class SpringBootAwsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootAwsApplication.class, args);
	}

}
