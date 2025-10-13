package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.Change_Prod.NewLastShtrihs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddCena_prod_handler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddKilkist_prod_handler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony_katalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class ChangeOpys_LastShtrih implements MessageHandle {

    @Autowired
    private ChangeOpys_handler changeOpys_handler;
    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(Message msgcallbackData) {
        return (changeOpys_handler.get_opyschange(msgcallbackData.getFrom().getId())!=null);
    }

    @Override
    public void handle(Message msg, TelegramLongPollingBot bot) {

        Long userId = msg.getFrom().getId();
        ChangeOpys_handler.RenameRequest request = changeOpys_handler.get_opyschange(userId);
        Vapecompony_katalog v_k = vapecomponyKatalogRepository.findByIdAndBot_Id(request.key, request.botId).get();



        ObjectMapper mapper = new ObjectMapper();
        try {
            String entitiesJson = mapper.writeValueAsString(msg.getEntities());
            if(changeOpys_handler.get_opyschange(userId)!=null){

                v_k.setDescription(msg.getText());
                v_k.setEntitiesDescJson(entitiesJson);

                vapecomponyKatalogRepository.save(v_k);
                changeOpys_handler.clear(userId);
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



            sendMessage.setText("✅Описание изменено");

            /*

            if(v_k.getEntitiesDescJson()!=null){
                try {
                    ObjectMapper mapper2 = new ObjectMapper();
                    List<MessageEntity> entities = mapper2.readValue(
                            v_k.getEntitiesDescJson(),
                            new TypeReference<List<MessageEntity>>() {}
                    );
                    sendMessage.setEntities(entities);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
*/


           // bot.execute(sendMessage);

            Message msgg =  bot.execute(sendMessage);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());


        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }
}
