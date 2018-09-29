package com.lee.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author lee
 * @date 2018/9/28
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public Mono<String> test() throws Exception {
        throw new  Exception("test");
//        return Mono.justOrEmpty("123");
    }
}
