package io.proj3ct.SpringDemoBot.DaO.CurrencyEditing;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.BotStateService;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.swing.*;

@Component
public class EditCurrency implements CallbackHandler {

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private BotStateService botStateService;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("EDIT_CURRENCY:");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {
        String botId = callbackDataPart(query.getData(), "EDIT_CURRENCY:");

       if(!cheking.mustCheck(Long.parseLong(botId), query.getMessage().getChatId(), bot)){return;};

        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);


        SendPhoto sendPhoto = new SendPhoto();

        sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));
        sendPhoto.setPhoto( new InputFile("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/curr.png"));
        sendPhoto.setCaption("\uD83C\uDFE6 Укажите валюту для отображения цен:");

        botStateService.expectButtonRename(query.getFrom().getId(), Long.valueOf(botId), "curr");

        try{


            Message m =  bot.execute(sendPhoto);
            messageRegistry.addMessage(m.getChatId(), m.getMessageId());

        }

        catch (TelegramApiException e){
            e.printStackTrace();
        }



    }

    private String callbackDataPart(String full, String prefix) {
        return full.substring(prefix.length());
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        InlineKeyboardButton b = new InlineKeyboardButton(text);
        b.setCallbackData(callbackData);
        return b;
    }
}
