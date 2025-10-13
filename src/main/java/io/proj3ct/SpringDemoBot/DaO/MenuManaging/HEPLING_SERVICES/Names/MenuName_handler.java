package io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Names;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MenuName_handler {

    private final Map<Long, RenameRequest> renameRequests = new HashMap<>();

    public void expectNameRename(Long userId, Long botId, Long key) {
        renameRequests.put(userId, new RenameRequest(botId, key));
    }

    public RenameRequest getNameRequest(Long userId) {
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
