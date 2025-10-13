package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts;


import io.proj3ct.SpringDemoBot.HelpingServise.Clear_exept;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AddCena_prod_handler {

    private final Map<Long, RenameRequest> renameRequests = new HashMap<>();

    @Autowired
    private Clear_exept clearExept;

    public void expect_cena_product(Long userId, Long botId, String key) {
        clearExept.deleteAll_exept_for(userId);
        renameRequests.put(userId, new RenameRequest(botId, key));
    }

    public RenameRequest get_cena_product(Long userId) {
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
