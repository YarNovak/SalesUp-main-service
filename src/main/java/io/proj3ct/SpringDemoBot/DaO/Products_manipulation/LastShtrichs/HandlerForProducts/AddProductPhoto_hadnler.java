package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts;

import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Photos.MenuPhoto_handler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AddProductPhoto_hadnler {
    private final Map<Long, RenameRequest> renameRequests = new HashMap<>();
    private final Map<Long, RenameRequest> addRequests = new HashMap<>();


    public void expectPhotoRename(Long userId, Long botId, Long key) {
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
        public final Long key;
        public RenameRequest(Long botId, Long key) {
            this.botId = botId;
            this.key = key;
        }
    }
    public void add(Long userId, Long botId, Long key){
        addRequests.put(userId, new RenameRequest(botId, key));
    }

    public RenameRequest getaddRequest(Long userId) {
        return addRequests.get(userId);

    }

    public void clear_add(Long userId) {
        addRequests.remove(userId);
    }
}
