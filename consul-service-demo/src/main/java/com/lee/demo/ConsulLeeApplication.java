package com.lee.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class ConsulLeeApplication {

	@PostMapping("/hi/lee")
	public String lee() {
		return "hi ,i'm lee";
	}

	@GetMapping("/hi")
	public DemoResponse hi(@RequestParam String name) {
		DemoResponse response = new DemoResponse();
		response.setName("Hi," + name);
		return response;
	}

	public static void main(String[] args) {
		SpringApplication.run(ConsulLeeApplication.class, args);
	}

	public class DemoResponse {
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
