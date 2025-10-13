package io.proj3ct.SpringDemoBot.Cash.Rabiit;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheInvalidationListener {

    private final CacheManager cacheManager;

    @RabbitListener(queues = "cache.queue")
    public void handleCacheEviction(CacheInvalidationMessage message) {
        if (message == null || message.getCacheName() == null || message.getKey() == null) return;

        var cache = cacheManager.getCache(message.getCacheName());
        if (cache != null) {
            cache.evict(message.getKey());
        }
    }
}
