package io.proj3ct.SpringDemoBot.DaO.MessagEditing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.BotMessage;
import io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.PhotoEditing.MStateServise;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.repository.BotMessageRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Component
public class RenameMessageHandler implements MessageHandle {

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private MStateServise stateServise;
    @Autowired
    private BotMessageRepository botMessageRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(org.telegram.telegrambots.meta.api.objects.Message msg) {
        return stateServise.getRenameRequest(msg.getFrom().getId()) != null;
    }

    @Override
    public void handle(org.telegram.telegrambots.meta.api.objects.Message msg, TelegramLongPollingBot bot) {
        Long userId = msg.getFrom().getId();
        MStateServise.RenameRequest request = stateServise.getRenameRequest(userId);
        if (request == null) return;

        Optional<Bot> botOpt = botRepository.findById(request.botId);
        if (botOpt.isEmpty()) return;

        Bot clientBot = botOpt.get();
         /*


        clientBot.getMessages().put(request.key, msg.getText());// JSON Map

        botRepository.save(clientBot);

        stateServise.clear(userId);


         */


        ObjectMapper mapper = new ObjectMapper();
        try {
            String entitiesJson = mapper.writeValueAsString(msg.getEntities());
            if(stateServise.getRenameRequest(userId)!=null){

                BotMessage botMessage = botMessageRepository.findByMessageKeyAndBot_Id(stateServise.getRenameRequest(userId).key, clientBot.getId()).get();
                botMessage.setText(msg.getText());
                botMessage.setEntitiesJson(entitiesJson);

                clientBot.setActive(false);
                botRepository.save(clientBot);

                botMessageRepository.save(botMessage);
                stateServise.clear(userId);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }



        try {

            /*

            Bot botik =  botRepository.findById(request.botId).get();
            botik.setActive(false);

            botRepository.save(botik);

             */

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(msg.getChatId().toString());
            sendMessage.setText("✅ Текст успешно обновлён");

            //sendMessage.setEntities(msg.getEntities()); // зберігає форматування
            
          //  bot.execute(sendMessage);

            Message msgg =  bot.execute(sendMessage);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());


        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
