package io.proj3ct.SpringDemoBot.DaO.ButtonEditing;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.BotStateService;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class RenameButtonCallbackHandler implements CallbackHandler {

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private BotStateService botStateService; // потрібен сервіс очікування вводу

    @Autowired
    private OwnerCheking cheking;

    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("RENAME_BUTTON:");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {




        String[] parts = query.getData().split(":");
        if (parts.length != 3) return;

        String key = parts[1];      // catalog, cart, ...
        Long botId = Long.parseLong(parts[2]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        botStateService.expectButtonRename(query.getFrom().getId(), botId, key); // чекаємо вводу

        SendMessage msg = new SendMessage();
        msg.setChatId(query.getMessage().getChatId().toString());



        msg.setText("✏\uFE0F Введите новое название кнопки:");

        try {

            Message msgg =  bot.execute(msg);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
