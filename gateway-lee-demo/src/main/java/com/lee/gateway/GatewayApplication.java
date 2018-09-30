package com.lee.gateway;

import com.lee.gateway.filter.CustomFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@EnableCircuitBreaker
public class GatewayApplication {

    @Bean
    public RouteLocator customerRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/test/prefix/**")
                        .filters(f -> f.stripPrefix(2)
                                .filter(new CustomFilter())
                                .addResponseHeader("X-Response-test", "test"))
                        .uri("http://localhost:4000")
                        .order(0)
                        .id("test_consumer_service")
                )
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
