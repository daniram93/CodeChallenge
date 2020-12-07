package com.OrderExecution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
public class OrderExecutionApplication {

	@Bean
	public Main schedulerRunner() {
		return new Main();
	}

	public static void main(String[] args) {
		SpringApplication.run(OrderExecutionApplication.class, args);
	}

}
