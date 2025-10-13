package io.proj3ct.SpringDemoBot.Dispetchers;


import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;

import io.proj3ct.SpringDemoBot.HelpingServise.Clear_exept;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CallbackDispatcher {

    @Autowired
    private List<CallbackHandler> handlers;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private Clear_exept clearExept;



    public void dispatch(CallbackQuery query, TelegramLongPollingBot bot) {


        for(CallbackHandler handler : handlers) {

            if(handler.support(query.getData())){
                 clearExept.deleteAll_exept_for(query.getFrom().getId());
                handler.handle(query, bot);
                return;
            }

        }
        SendMessage fallback = new SendMessage(String.valueOf(query.getMessage().getChatId()), "⚠\uFE0F Неизвестное действие");

        try {

          Message msgg =  bot.execute(fallback);messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

}
