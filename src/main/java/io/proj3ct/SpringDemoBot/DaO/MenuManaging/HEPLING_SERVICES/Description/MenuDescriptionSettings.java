package io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Description;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class MenuDescriptionSettings implements MessageHandle {

    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private MenuDescription_handler menuDescription_handler;

    @Autowired
    private MessageRegistry messageRegistry;


    @Override
    public boolean support(org.telegram.telegrambots.meta.api.objects.Message msg) {
        return (menuDescription_handler.getDescriptionRequest(msg.getFrom().getId())!=null);
    }

    @Override
    public void handle(org.telegram.telegrambots.meta.api.objects.Message msg, TelegramLongPollingBot bot) {

        Long userId = msg.getFrom().getId();
        MenuDescription_handler.RenameRequest renameRequest = menuDescription_handler.getDescriptionRequest(userId);
        Vapecompony vapecompony = vapecomponyRepository.findByIdAndBot_Id(renameRequest.key, renameRequest.botId).get();


        ObjectMapper mapper = new ObjectMapper();
        try {
            String entitiesJson = mapper.writeValueAsString(msg.getEntities());
            if(menuDescription_handler.getDescriptionRequest(userId)!=null){

                vapecompony.setDescription(msg.getText());
                vapecompony.setEntitiesDescJson(entitiesJson);

                vapecomponyRepository.save(vapecompony);
                menuDescription_handler.clear(userId);
            }
            menuDescription_handler.clear(userId);

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



            sendMessage.setText("✅Описание изменено");


/*
            if(vapecompony.getEntitiesJson()!=null){
                try {
                    ObjectMapper mapper2 = new ObjectMapper();
                    List<MessageEntity> entities = mapper2.readValue(
                            vapecompony.getEntitiesDescJson(),
                            new TypeReference<List<MessageEntity>>() {}
                    );
                    sendMessage.setEntities(entities);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }


 */

          //  bot.execute(sendMessage);


            Message msgg =  bot.execute(sendMessage);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());


        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
