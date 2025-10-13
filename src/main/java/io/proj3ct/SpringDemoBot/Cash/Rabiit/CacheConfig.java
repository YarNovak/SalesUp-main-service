package io.proj3ct.SpringDemoBot.Cash.Rabiit;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .maximumSize(500);
        CaffeineCacheManager manager = new CaffeineCacheManager("vapecomponyCache", "botCache", "anotherCache");
        manager.setCaffeine(caffeineBuilder);
        return manager;

    }
}
