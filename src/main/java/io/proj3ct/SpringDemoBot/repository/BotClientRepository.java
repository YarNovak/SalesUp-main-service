package io.proj3ct.SpringDemoBot.repository;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.BotClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface BotClientRepository extends CrudRepository<BotClient, Long> {
    Optional<BotClient> findByBotAndChatId(Bot bot, Long chatId);
    List<BotClient> findByBot(Bot bot);
}
