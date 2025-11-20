package io.proj3ct.SpringDemoBot.HelpingServise.Admin;

import io.proj3ct.SpringDemoBot.config.AdminConfig;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Small helper to check whether a message comes from the configured bot owner (admin).
 * Implemented as a Spring component that stores BotConfig into a static field so
 * it can be used from interface default methods.
 */
@Component
public class AdminUtils {

    @Autowired
    private static AdminConfig config;

    public static boolean isAdmin(Message msg){
        if(msg == null || msg.getFrom() == null || config == null) return false;
        try{
            List<Long> adminIds = config.getAdminIds();
            if(adminIds == null) return false;
            return adminIds.contains(msg.getFrom().getId());
        }
        catch(Throwable t){
            return false;
        }
    }

}
