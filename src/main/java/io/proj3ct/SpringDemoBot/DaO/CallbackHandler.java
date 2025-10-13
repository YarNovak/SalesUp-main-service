package io.proj3ct.SpringDemoBot.DaO;

import javassist.tools.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackHandler {

    boolean support(String callbackData);
    void handle(CallbackQuery query, TelegramLongPollingBot bot);

}
