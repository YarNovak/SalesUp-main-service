package io.proj3ct.SpringDemoBot.service;

import io.proj3ct.SpringDemoBot.Cash.Rabiit.CacheInvalidationSender;
import io.proj3ct.SpringDemoBot.Cash.Rabiit.RabbitMQConfig;
import io.proj3ct.SpringDemoBot.Cash.VapecomponyRepository_working_withBD.VapecomponyDTO;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VapecomponyService {

    private final VapecomponyRepository repository;
    private final CacheInvalidationSender cacheInvalidationSender;
    private final BotConfig config;
    private final BotRepository botRepository;

    @Cacheable(value = "vapecomponyCache", key = "#id")
    public Vapecompony getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "vapecomponyCache", key = "#vapecompony.id")
            }
    )
    public Vapecompony save(VapecomponyDTO dto) {
        Vapecompony vapecompony = new Vapecompony(dto.getName(), dto.getMessageLink());
        Vapecompony saved = repository.save(vapecompony);
        cacheInvalidationSender.sendInvalidation("vapecomponyCache", String.valueOf(saved.getId()));
        return saved;
    }

    @CacheEvict(value = "vapecomponyCache", key = "#id")
    public void delete(Long id) {
        repository.deleteById(id);
        cacheInvalidationSender.sendInvalidation("vapecomponyCache", String.valueOf(id));
    }


    @CacheEvict(value = "vapecomponyCache", key = "#id")
    public void evictCache(Long id) {
        // Цей метод викликається коли приходить повідомлення з RabbitMQ
    }


}
