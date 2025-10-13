package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts;

import io.proj3ct.SpringDemoBot.HelpingServise.Clear_exept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Add_prod_handler {

    private final Map<Long, Long> addRequests = new HashMap<>();

    @Autowired
    private Clear_exept clearExept;

    public void expect_new_product(Long userId, Long botId){
        clearExept.deleteAll_exept_for(userId);
        addRequests.put(userId, botId);
    }
    public Long get_new_product(Long userId){
        return addRequests.get(userId);
    }
    public void clear(Long userId) {
        addRequests.remove(userId);
    }

}
