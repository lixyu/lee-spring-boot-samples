package com.lee.gateway.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

import java.util.Map;

@Configuration
@Slf4j
public class GatewayConfig {

  /**
   * 按接口限流
   *
   * @return
   */
  @Bean("apiKeyResolver")
  @Primary
  public KeyResolver apiKeyResolver() {
    return exchange -> Mono.just(exchange.getRequest().getPath().value());
  }

  /**
   * 按请求IP限流
   *
   * @return
   */
  @Bean
  public KeyResolver remoteAddrKeyResolver() {
    return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
  }

  @Bean
  @ConfigurationProperties(prefix = "gateway.audit")
  public AuditProperties getAuditProperties() {
    return new AuditProperties();
  }

  @Bean
  @Order(-1)
  public GlobalFilter a() {
    return (exchange, chain) -> {
      Map map = exchange.getRequest().getQueryParams().toSingleValueMap();
      log.info("请求的入参是:" + JSON.toJSONString(map));
      log.info("first pre filter");
      return chain
          .filter(exchange)
          .then(
              Mono.fromRunnable(
                  () -> {
                    int httpStatus = exchange.getResponse().getStatusCode().value();
                    log.info("第三方返回状态码:" + httpStatus);
                  }));
    };
  }
}
