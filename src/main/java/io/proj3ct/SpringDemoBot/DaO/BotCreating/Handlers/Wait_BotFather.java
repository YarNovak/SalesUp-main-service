package io.proj3ct.SpringDemoBot.DaO.BotCreating.Handlers;

import io.proj3ct.SpringDemoBot.HelpingServise.Clear_exept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class Wait_BotFather {

    @Autowired
    private Clear_exept clearExept;

    private final Set<Long> token = new HashSet<>();

    public void clear(Long userId) {
        token.remove(userId);
    }

    public void expect_token(Long userId) {
        clearExept.deleteAll_exept_for(userId);
        token.add(userId);
    }
    public boolean iswaiting_foruser(Long userId){

        return token.contains(userId);

    }



}
