package io.proj3ct.SpringDemoBot.repository;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.DiscountProgress;
import io.proj3ct.SpringDemoBot.DB_entities.PlatformUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiscountProgressRepository extends JpaRepository<DiscountProgress, Long> {
    List<DiscountProgress> findByUser(PlatformUser user);
    Optional<DiscountProgress> findByUserAndBot(PlatformUser user, Bot bot);
}