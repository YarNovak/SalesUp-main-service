package io.proj3ct.SpringDemoBot.repository;

import io.proj3ct.SpringDemoBot.DB_entities.PlatformUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlatformUserRepository extends JpaRepository<PlatformUser, Long> {
    Optional<PlatformUser> findByTelegramId(Long telegramId);
    List<PlatformUser> findByReferredBy(PlatformUser referrer);
    Optional<PlatformUser> findById(Long id);
}