package com.lee.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cloud.gateway.filter.ratelimit.AbstractRateLimiter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.Min;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author lee
 * @date 2018/9/29
 */

@Slf4j
@Primary
@Component
public class RateLimiter extends AbstractRateLimiter<RateLimiter.Config> implements ApplicationContextAware {
    public static final String CONFIGURATION_PROPERTY_NAME = "redis-rate-limiter";
    public static final String REDIS_SCRIPT_NAME = "redisRequestRateLimiterScript";
    public static final String RATE_CONFIG_REPLENISH = "replenish";
    public static final String RATE_CONFIG_CAPACITY = "capacity";

    private ReactiveRedisTemplate<String, String> redisTemplate;
    private RedisScript<List<Long>> script;
    private AtomicBoolean initialized = new AtomicBoolean(false);
    private static final Map<String, Config> CONFIGS = new HashMap<>();

    /**
     * 初始化限流配置
     */
    public void setConfig(String path, int replenishRate, int burstCapacity) {
        if (CONFIGS.containsKey(path)) {
            if (CONFIGS.get(path).getBurstCapacity() != burstCapacity || CONFIGS.get(path).getReplenishRate() != replenishRate) {
                CONFIGS.replace(path, new Config().setReplenishRate(replenishRate).setBurstCapacity(burstCapacity));
            }
        } else {
            CONFIGS.put(path, new Config().setReplenishRate(replenishRate).setBurstCapacity(burstCapacity));
        }
    }

    public RateLimiter() {
        super(Config.class, CONFIGURATION_PROPERTY_NAME, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        if (initialized.compareAndSet(false, true)) {
            this.redisTemplate = context.getBean("stringReactiveRedisTemplate", ReactiveRedisTemplate.class);
            this.script = context.getBean(REDIS_SCRIPT_NAME, RedisScript.class);
            if (context.getBeanNamesForType(Validator.class).length > 0) {
                this.setValidator(context.getBean(Validator.class));
            }
        }
    }

    @Override
    public Mono<Response> isAllowed(String routeId, String path) {
        if (!this.initialized.get()) {
            throw new IllegalStateException("RateCheckRedisRateLimiter is not initialized");
        }

        Config routeConfig = getConfig(routeId, path);

        int replenishRate = routeConfig.getReplenishRate();
        int burstCapacity = routeConfig.getBurstCapacity();

        try {
            List<String> keys = getKeys(path);

            List<String> scriptArgs = Arrays.asList(String.valueOf(replenishRate), String.valueOf(burstCapacity),
                    String.valueOf(Instant.now().getEpochSecond()), "1");

            Flux<List<Long>> flux = this.redisTemplate.execute(this.script, keys, scriptArgs);

            return flux.onErrorResume(throwable -> Flux.just(Arrays.asList(1L, -1L)))
                    .reduce(new ArrayList<Long>(), (longs, l) -> {
                        longs.addAll(l);
                        return longs;
                    }).map(results -> {
                        boolean allowed = results.get(0) == 1L;
                        Long tokensLeft = results.get(1);
                        HashMap<String,String> headers = new HashMap<String,String>(2){{
                            put(RATE_CONFIG_REPLENISH, String.valueOf(tokensLeft));
                            put(RATE_CONFIG_CAPACITY, String.valueOf(burstCapacity));
                        }};
                        return new Response(allowed, headers);
                    });
        } catch (Exception e) {
            log.error("redis发生异常[{}]", e);
        }
        return Mono.just(new Response(true, -1));
    }

    /**
     * 获取对应path的配置信息
     * @return
     */
    private Config getConfig(String routeId, String path) {
        Config routeConfig;
        if (CONFIGS.containsKey(path)) {
            routeConfig = CONFIGS.get(path);
        } else {
            routeConfig = CONFIGS.get(routeId);
        }
        return routeConfig;
    }

    /**
     * 根据path获取对应存储到redis的key
     */
    private List<String> getKeys(String id) {
        String pattern = "request_rate_limiter.{%s}.%s";

        return new ArrayList<String>() {{
            add(String.format(pattern, id, "tokens"));
            add(String.format(pattern, id, "timestamp"));
        }};
    }

    @Validated
    public static class Config {
        @Min(1)
        private int replenishRate;

        @Min(0)
        private int burstCapacity = 0;

        int getReplenishRate() {
            return replenishRate;
        }

        Config setReplenishRate(int replenishRate) {
            this.replenishRate = replenishRate;
            return this;
        }

        int getBurstCapacity() {
            return burstCapacity;
        }

        Config setBurstCapacity(int burstCapacity) {
            this.burstCapacity = burstCapacity;
            return this;
        }

        @Override
        public String toString() {
            return "Config{" +
                    "replenishRate=" + replenishRate +
                    ", burstCapacity=" + burstCapacity +
                    '}';
        }
    }
}

