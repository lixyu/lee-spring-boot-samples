package com.lee.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lee
 * @date 2018/9/30
 */
@Slf4j
@Component
public class CustomFilter extends AbstractGatewayFilterFactory{

    private static  final String COUNT_START_TIME="countStartTime";

    @Override
    public GatewayFilter apply(Object config) {

        return (exchange, chain) -> {
           exchange.getAttributes().put(COUNT_START_TIME,System.currentTimeMillis());
           return chain.filter(exchange).then(Mono.fromRunnable(()->{
                Long startTime=exchange.getAttribute(COUNT_START_TIME);
                Long runTime=System.currentTimeMillis()-startTime;
                log.info(exchange.getRequest().getURI().getRawPath() + ": " + runTime + "ms");
            }));

        };
    }
}
