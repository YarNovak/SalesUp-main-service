package io.proj3ct.SpringDemoBot.DaO.MessagEditing.BotFatherSettings.BotFatherHandlers;

import io.proj3ct.SpringDemoBot.HelpingServise.Clear_exept;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FatherSettings_handler {

    @Autowired
    private Clear_exept clearExept;

    private final Map<Long,RenameRequest> resetRequests = new HashMap<>();

    public void expectRename(Long userId, Long bot_id, String key){

        clearExept.deleteAll_exept_for(userId);
        resetRequests.put(userId, new RenameRequest(bot_id, key));

    }
    public RenameRequest getResetRequest(Long userId){

        return resetRequests.get(userId);

    }
    public void clear(Long userId) {
        resetRequests.remove(userId);
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
