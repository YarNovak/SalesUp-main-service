package io.proj3ct.SpringDemoBot.DaO.EditMassages;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.Dispetchers.CallbackDispatcher;
import io.proj3ct.SpringDemoBot.HelpingServise.Clear_exept;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class Totaly_end implements CallbackHandler {

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private Clear_exept exept;

    @Override
    public boolean support(String callbackData) {
        return callbackData.equals("TOTALLY_END");

    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        exept.deleteAll_exept_for(query.getFrom().getId());
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), true, bot);

    }
}
