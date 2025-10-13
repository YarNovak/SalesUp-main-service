package io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.TextsEditing;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Text_handler {

    private final Map<Long, RenameRequest> renameRequests = new HashMap<>();
    public void expectPhotoRename(Long userId, Long botId, String key) {
        renameRequests.put(userId, new RenameRequest(botId, key));
    }

    public RenameRequest getRenameRequest(Long userId) {
        return renameRequests.get(userId);
    }

    public void clear(Long userId) {
        renameRequests.remove(userId);
    }

    @Setter
    @Getter
    public static class RenameRequest {
        public final Long botId;
        public final String key;
        public RenameRequest(Long botId, String key) {
            this.botId = botId;
            this.key = key;
        }
    }

}
