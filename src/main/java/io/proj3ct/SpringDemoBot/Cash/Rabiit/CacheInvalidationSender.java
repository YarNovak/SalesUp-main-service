package io.proj3ct.SpringDemoBot.Cash.Rabiit;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheInvalidationSender {

    private final RabbitTemplate rabbitTemplate;

    public void sendInvalidation(String cacheName, String key) {
        CacheInvalidationMessage message = new CacheInvalidationMessage(cacheName, key);
        rabbitTemplate.convertAndSend("cache.exchange", "cache.invalidate", message);
    }
}