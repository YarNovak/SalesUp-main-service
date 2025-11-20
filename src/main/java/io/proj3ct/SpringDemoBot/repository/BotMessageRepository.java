package io.proj3ct.SpringDemoBot.repository;

import com.google.api.client.util.ObjectParser;
import io.proj3ct.SpringDemoBot.DB_entities.BotMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BotMessageRepository extends JpaRepository<BotMessage, Long> {

    boolean existsByMessageKeyAndBot_Id(final String messageKey, final Long botId);
    Optional<BotMessage> findByMessageKeyAndBot_Id(final String messageKey, final Long botId);
    void deleteByMessageKeyAndBot_Id(final String messageKey, final Long botId);
    void deleteAllByBot_Id(final Long botId);

    @Modifying
    @Transactional
    @Query("DELETE FROM BotMessage m WHERE m.bot.id = :botId")
    void deleteBulkByBotId(@Param("botId") Long botId);
}
