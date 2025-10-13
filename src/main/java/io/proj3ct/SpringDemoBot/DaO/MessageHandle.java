package io.proj3ct.SpringDemoBot.DaO;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface MessageHandle {

    boolean support(org.telegram.telegrambots.meta.api.objects.Message msgcallbackData);
    void handle(org.telegram.telegrambots.meta.api.objects.Message msg, TelegramLongPollingBot bot);

}
