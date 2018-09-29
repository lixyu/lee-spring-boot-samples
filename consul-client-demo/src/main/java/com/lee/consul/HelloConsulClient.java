package com.lee.consul;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("consul-lee")
public interface HelloConsulClient {
	@GetMapping("/lee")
	String helloConsul();

	@GetMapping("/hi")
	String hi(@RequestParam(value = "name") String name);

}
