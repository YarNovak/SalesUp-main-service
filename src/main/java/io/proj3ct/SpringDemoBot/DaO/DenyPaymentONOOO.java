package io.proj3ct.SpringDemoBot.DaO;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.HelpingServise.CleanBot.CleanTheBot;
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
public class DenyPaymentONOOO implements CallbackHandler{

    @Autowired
    private CleanTheBot cleanTheBot;
    @Autowired
    private BotRepository botRepository;
    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("PayDeny:");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String parts[] = query.getData().split(":");

        Long bot_id = Long.valueOf(parts[1]);

        if(!cheking.mustCheck(bot_id, query.getMessage().getChatId(), bot)){return;};

        Bot bt =botRepository.findById(bot_id).get();
        cleanTheBot.base_settingsForBot(bt);

        SendMessage sn = new SendMessage();
        sn.setChatId(query.getMessage().getChatId().toString());
        sn.setParseMode("HTML");
        sn.setText(
                "Подписка была отменена \uD83D\uDEAB\n" +
                "\n" +
                "<i>Бот и его содержимое были удалены</i> \uD83D\uDDD1"+"\n" +
                "\n" +
                "Будь с SalesUp и возвращайся, когда захочешь\uD83E\uDD29");

        try {
           Message msgThisOne =  bot.execute(sn);
           messageRegistry.addMessage(msgThisOne.getChatId(), msgThisOne.getMessageId());
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
