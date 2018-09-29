package com.lee.consul;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ConsulClientDemoController {

	@Autowired
	private ConsulDiscoveryClient consulDiscoveryClient;

	@Autowired
	private HelloConsulClient helloConsulCient;

	@GetMapping("/hello")
	public String hello() {

		// discoveryClient.getServices();
		return helloConsulCient.helloConsul();
	}

	@GetMapping("/hi")
	public String hi() {

		// discoveryClient.getServices();
		return helloConsulCient.hi("consul");
	}

	@GetMapping("/client")
	public String restClient() {
		List<ServiceInstance> serviceInstanceList = consulDiscoveryClient.getInstances("consul-lee");
		ServiceInstance serviceInstance = serviceInstanceList.get(0);
		System.out.println("服务地址：" + serviceInstance.getUri());
		System.out.println("服务名称：" + serviceInstance.getServiceId());

		String callServiceResult = new RestTemplate().getForObject(serviceInstance.getUri().toString() + "/hi",
				String.class);
		System.out.println(callServiceResult);

		return "hello";
	}

	@GetMapping("/ribbon")

	public String ribbon() {

		return "";
	}

}
