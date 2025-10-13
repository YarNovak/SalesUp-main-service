package io.proj3ct.SpringDemoBot.repository;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.BotSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BotSettingsRepository extends JpaRepository<BotSettings, Long> {
    Optional<BotSettings> findByBot(Bot bot);
}
