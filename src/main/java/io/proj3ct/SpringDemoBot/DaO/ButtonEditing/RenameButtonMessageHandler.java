package io.proj3ct.SpringDemoBot.DaO.ButtonEditing;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.HelpingServise.BotStateService;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Component
public class RenameButtonMessageHandler implements MessageHandle {

    @Autowired
    private BotRepository botRepository;
    @Autowired
    private BotStateService botStateService;
    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(org.telegram.telegrambots.meta.api.objects.Message msg) {
        return botStateService.getRenameRequest(msg.getFrom().getId()) != null;
    }

    @Override
    public void handle(org.telegram.telegrambots.meta.api.objects.Message msg, TelegramLongPollingBot bot) {
        Long userId = msg.getFrom().getId();
        BotStateService.RenameRequest request = botStateService.getRenameRequest(userId);
        if (request == null) return;

        Optional<Bot> botOpt = botRepository.findById(request.botId);
        if (botOpt.isEmpty()) return;

        Bot clientBot = botOpt.get();

         clientBot.getButtonTexts().put(request.key, msg.getText()); // JSON Map
        clientBot.setActive(false);
        botRepository.save(clientBot);

        botStateService.clear(userId);

        try {

            Bot botik =  botRepository.findById(request.botId).get();
            botik.setActive(false);

            botRepository.save(botik);
            Message msgg;
            if(request.key.equals("curr")){
                msgg = bot.execute(new SendMessage(msg.getChatId().toString(),
                        "✅ Валюту изменено на: " + msg.getText()));
            }
            else{
                msgg = bot.execute(new SendMessage(msg.getChatId().toString(),
                        "✅ Название кнопки изменено на: " + msg.getText()));
            }



            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
