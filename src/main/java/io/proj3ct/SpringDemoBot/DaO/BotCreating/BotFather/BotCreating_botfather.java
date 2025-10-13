package io.proj3ct.SpringDemoBot.DaO.BotCreating.BotFather;


import io.proj3ct.SpringDemoBot.DaO.BotCreating.Handlers.Wait_BotFather;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class BotCreating_botfather implements CallbackHandler {


    @Autowired
    private Wait_BotFather waitBotFather;

    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(String callbackData) {
        return "CREATE_BOTFATHER".equals(callbackData);
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {

            waitBotFather.expect_token(query.getFrom().getId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Якшо не даун то знаєш як відіслати токен))");
        sendMessage.setChatId(query.getMessage().getChatId().toString());

        try{

            Message msg =  bot.execute(sendMessage);
            messageRegistry.addMessage(msg.getChatId(), msg.getMessageId());
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }


}
