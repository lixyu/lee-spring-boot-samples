package com.lee.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.lee.gateway.bean.JsonResult;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Min;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Lee
 */
@Slf4j
@Component
public class RateLimitFilter extends AbstractGatewayFilterFactory<RateLimitFilter.Config> {

    private static final String RATE_LIMIT_TOTAL = "X-RateLimit-Total";
    private static final String RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";

    private final RateLimiter rateLimiter;

    private final KeyResolver keyResolver;

    public RateLimitFilter(RateLimiter rateLimiter, KeyResolver keyResolver) {
        super(RateLimitFilter.Config.class);
        this.rateLimiter = rateLimiter;
        this.keyResolver = keyResolver;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("replenishRate", "burstCapacity", "path");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            Route route = Objects.requireNonNull(exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR));
            initRateLimitConfig(config, route);
            return keyResolver.resolve(exchange).flatMap(key ->
                    rateLimiter.isAllowed(route.getId(), key).flatMap(response -> {
                        Map<String,String> headers = response.getHeaders();
                        exchange.getResponse().getHeaders()
                                .add(RATE_LIMIT_REMAINING, headers.get(RateLimiter.RATE_CONFIG_REPLENISH));
                        exchange.getResponse().getHeaders()
                                .add(RATE_LIMIT_TOTAL, headers.get(RateLimiter.RATE_CONFIG_CAPACITY));
                        if (response.isAllowed()) {
                            return chain.filter(exchange);
                        }
                        return setRateCheckResponse(exchange.getResponse());
                    }));
        };
    }

    /**
     * 初始化限流配置，优先获取config.path匹配限流节点，若不指定config.path，则以ServiceId作为Redis限流的Key
     */
    private void initRateLimitConfig(Config config, Route route) {
        if (StringUtils.isBlank(config.getPath())) {
            rateLimiter.setConfig(route.getId(), config.getReplenishRate(), config.getBurstCapacity());
        } else {
            rateLimiter.setConfig(config.getPath(), config.getReplenishRate(), config.getBurstCapacity());
        }
    }

    /**
     * 超过限流的量，返回给前端的信息
     */
    private Mono<Void> setRateCheckResponse(ServerHttpResponse response) {
        //response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        DataBuffer bodyDataBuffer = response.bufferFactory().wrap(JSON.toJSONString(JsonResult.errorResponse("系统繁忙，请稍后重试")).getBytes());
        return response.writeWith(Mono.just(bodyDataBuffer));
    }

    @Validated
    @Data
    @ToString
    public static class Config {
        @Min(value = 1, message = "path流量最小为1")
        private int replenishRate;
        @Min(value = 0, message = "path总容量最小为0")
        private int burstCapacity;
        private String path;
    }
}
