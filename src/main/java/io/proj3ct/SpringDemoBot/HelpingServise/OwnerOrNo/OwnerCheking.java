package io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo;


import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.repository.PlatformUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Service
public class OwnerCheking {

    @Autowired
    public BotRepository botRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private PlatformUserRepository platformUserRepository;

    private boolean owner_or_no(Long bot_id, Long user_id){

        Optional<Bot> optBot = botRepository.findById(bot_id);
        if(optBot.isPresent()){
            Bot bot = optBot.get();
            return bot.getOwner().getTelegramId().equals(user_id);

        }
        return false;

    }

    public boolean mustCheck(Long bot_id, Long user_id, TelegramLongPollingBot bot){

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user_id.toString());
        sendMessage.setText("⚠\uFE0F Только владелец может изменять настройки и функционал");

        if(owner_or_no(bot_id, user_id)){
            System.out.println("IDSHNIK = " + owner_or_no(bot_id, user_id));
            return true;
        }

        try{
            Message msgg =  bot.execute(sendMessage);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
        }
        catch(TelegramApiException e){
            e.printStackTrace();
        }


        return false;

    }

}
