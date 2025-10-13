package io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Names;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Description.MenuDescription_handler;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class MenuNameSetting implements MessageHandle {


    @Autowired
    private BotRepository botRepository;

    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private MenuName_handler menuName_handler;

    @Autowired
    private MessageRegistry messageRegistry;


    @Override
    public boolean support(org.telegram.telegrambots.meta.api.objects.Message msg) {

        return (menuName_handler.getNameRequest(msg.getFrom().getId()) != null);

    }

    @Override
    public void handle(org.telegram.telegrambots.meta.api.objects.Message msg, TelegramLongPollingBot bot) {

        Long userId = msg.getFrom().getId();
        MenuName_handler.RenameRequest renameRequest = menuName_handler.getNameRequest(userId);

        Vapecompony vapecompony = vapecomponyRepository.findByIdAndBot_Id(renameRequest.key, renameRequest.botId).get();


        ObjectMapper mapper = new ObjectMapper();
        try {
            String entitiesJson = mapper.writeValueAsString(msg.getEntities());
            if(menuName_handler.getNameRequest(userId)!=null){

                vapecompony.setName(msg.getText());
                vapecompony.setEntitiesJson(entitiesJson);

                vapecomponyRepository.save(vapecompony);
                menuName_handler.clear(userId);
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



                sendMessage.setText("✅Название изменено");


/*
                if(vapecompony.getEntitiesJson()!=null){
                    try {
                        ObjectMapper mapper2 = new ObjectMapper();
                        List<MessageEntity> entities = mapper2.readValue(
                                vapecompony.getEntitiesJson(),
                                new TypeReference<List<MessageEntity>>() {}
                        );
                        sendMessage.setEntities(entities);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
*/


            Message msgg =  bot.execute(sendMessage);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());


        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
