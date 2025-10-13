package io.proj3ct.SpringDemoBot.DaO;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface CommandHandler {

    boolean support(String command);
    void handle(Message message, TelegramLongPollingBot bot);

}
