package io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Description;

import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Names.MenuName_handler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MenuDescription_handler {

    private final Map<Long, RenameRequest> renameRequests = new HashMap<>();

    public void expectDescriptionRename(Long userId, Long botId, Long key) {
        renameRequests.put(userId, new RenameRequest(botId, key));
    }

    public RenameRequest getDescriptionRequest(Long userId) {
        return renameRequests.get(userId);
    }

    public void clear(Long userId) {
        renameRequests.remove(userId);
    }

    @Setter
    @Getter
    public static class RenameRequest {
        public final Long botId;
        public final Long key;
        public RenameRequest(Long botId, Long key) {
            this.botId = botId;
            this.key = key;
        }
    }

}
