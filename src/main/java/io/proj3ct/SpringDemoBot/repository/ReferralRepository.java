package io.proj3ct.SpringDemoBot.repository;

import io.proj3ct.SpringDemoBot.DB_entities.PlatformUser;
import io.proj3ct.SpringDemoBot.DB_entities.Referral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReferralRepository extends JpaRepository<Referral, Long> {
    List<Referral> findByReferrer(PlatformUser referrer);
    Optional<Referral> findByReferredUser(PlatformUser referredUser);
}