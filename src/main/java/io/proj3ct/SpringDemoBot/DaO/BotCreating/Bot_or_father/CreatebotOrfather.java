package io.proj3ct.SpringDemoBot.DaO.BotCreating.Bot_or_father;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class CreatebotOrfather implements CallbackHandler {

    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(String callbackData) {
        return callbackData.equals("CREATE_BOT");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {


        SendMessage message = new SendMessage();
        message.setChatId(query.getMessage().getChatId().toString());
        message.setText("Вибери підходяий для себе метод");


        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(button("🔘 1 click method", "CREATE_BOT_EASILY"),
                button("🔘 BotFather method", "CREATE_BOTFATHER")));


        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);

        message.setReplyMarkup(markup);

        try{

           Message msg =  bot.execute(message);
           messageRegistry.addMessage(msg.getChatId(), msg.getMessageId());
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private InlineKeyboardButton button(String text, String key) {

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(key);
        return button;
    }
}
