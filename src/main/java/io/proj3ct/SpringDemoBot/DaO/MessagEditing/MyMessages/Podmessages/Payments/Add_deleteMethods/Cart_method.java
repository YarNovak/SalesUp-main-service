package io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.Podmessages.Payments.Add_deleteMethods;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public class Cart_method implements CallbackHandler {
    @Override
    public boolean support(String callbackData) {
        return false;
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {

    }
}
