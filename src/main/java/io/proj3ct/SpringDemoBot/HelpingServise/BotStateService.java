package io.proj3ct.SpringDemoBot.HelpingServise;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BotStateService {
    private final Map<Long, RenameRequest> renameRequests = new HashMap<>();

    public void expectButtonRename(Long userId, Long botId, String key) {
        renameRequests.put(userId, new RenameRequest(botId, key));
    }

    public RenameRequest getRenameRequest(Long userId) {
        return renameRequests.get(userId);
    }

    public void clear(Long userId) {
        renameRequests.remove(userId);
    }

    public static class RenameRequest {
        public final Long botId;
        public final String key;
        public RenameRequest(Long botId, String key) {
            this.botId = botId;
            this.key = key;
        }
    }
}
