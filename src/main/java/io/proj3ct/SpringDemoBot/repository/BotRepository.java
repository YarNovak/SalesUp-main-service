package io.proj3ct.SpringDemoBot.repository;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.PlatformUser;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BotRepository extends JpaRepository<Bot, Long> {
    List<Bot> findByOwner(PlatformUser owner);
    List<Bot> findByOwner_TelegramId(long telegramId);
    Optional<Bot> findByBotToken(String botToken);
    Optional<Bot> findById(long id);

    //List<Bot> findByCurrentPrice(long price);

    List<Bot> findAllByCurrentPrice(double price);

    Optional<Bot> findByIdAndOwner_TelegramId(long id, long ownerId);

    Optional<Bot> findFirstByOwnerIsNull();
    Optional<Bot> findFirstByOwnerIsNullOrderByIdAsc();
    Optional<Bot> findByBotusername(String botUsername);
   // Optional<Bot> findBy(Long botId);
}
