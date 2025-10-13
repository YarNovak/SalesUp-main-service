package io.proj3ct.SpringDemoBot.DaO.MenuManaging.Adding;

import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Videos.MenuVideo_handler;
import io.proj3ct.SpringDemoBot.HelpingServise.Clear_exept;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AddMenu_handler {

    private final Map<Long, Long> addRequests = new HashMap<>();

    @Autowired
    private Clear_exept clearExept;

    public void expect_new_vapecompony(Long userId, Long botId){
        clearExept.deleteAll_exept_for(userId);
            addRequests.put(userId, botId);
    }
    public Long get_new_vapecompony(Long userId){
        return addRequests.get(userId);
    }
    public void clear(Long userId) {
        addRequests.remove(userId);
    }


}
