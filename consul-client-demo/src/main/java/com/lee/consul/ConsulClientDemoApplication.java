package com.lee.consul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ConsulClientDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsulClientDemoApplication.class, args);
	}
}
