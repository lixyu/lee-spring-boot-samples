package com.lee.ai.aip.controller;

import com.lee.ai.aip.bean.JsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloBaiduController {

	@GetMapping("/baidu/hello")
	public String hello(@RequestParam String name) {

		System.out.println("000000000000000000000000");
		return "Hello " + name;
	}

	@PostMapping("/baidu/lee")
	public String lee(@RequestParam String name) {

		return "Hello " + name;
	}

	@PostMapping("/baidu/json")
	public JsonResult json(@RequestParam String name) {

		return JsonResult.successResponse("Hello " + name);
	}
}
