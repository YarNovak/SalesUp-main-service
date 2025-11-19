package io.proj3ct.SpringDemoBot.repository;

import com.google.api.client.util.ObjectParser;
import io.proj3ct.SpringDemoBot.DB_entities.BotMessageTextsDef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BotDefTextRepository extends JpaRepository<BotMessageTextsDef, String > {

    Optional<BotMessageTextsDef> findByMessageKey(String messageKey);
    List<BotMessageTextsDef> findAll();
}
