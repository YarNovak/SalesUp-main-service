package io.proj3ct.SpringDemoBot.Dispetchers;

import io.proj3ct.SpringDemoBot.DaO.CommandHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandDispatcher {

    @Autowired
    private final List<CommandHandler> handlers;

    @Autowired
    private MessageRegistry messageRegistry;

    public void dispatch(Message message, TelegramLongPollingBot bot) {

        String text = message.getText();
        for(CommandHandler handler : handlers) {
            if(handler.support(text)) {
                handler.handle(message, bot);
                return;
            }
        }

        SendMessage fallback = new SendMessage(String.valueOf(message.getChatId()), "⚠\uFE0F Неизвестная команда");
        try{


           // bot.execute();

            Message msgg =  bot.execute(fallback);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
        }
        catch(TelegramApiException e) {
            e.printStackTrace();
        }



    }

}

