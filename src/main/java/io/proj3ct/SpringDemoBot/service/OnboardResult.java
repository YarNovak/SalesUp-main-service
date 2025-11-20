package io.proj3ct.SpringDemoBot.service;

import java.util.List;
import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Result object for onboarding a batch of bots.
 * Contains lists of created bots, tokens that already existed, and tokens that were invalid.
 */

@Getter @Setter
@AllArgsConstructor
public class OnboardResult {
    private final List<Bot> created;
    private final List<String> existed;
    private final List<String> notValid;
}
