package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.Change_Prod.NewLastShtrihs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.Change_Prod.Update_Handlers.Changename_handler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddKilkist_prod_handler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony_katalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ChangeName_LastShrih implements MessageHandle {

    @Autowired
    private Changename_handler changenameHandler;


    @Autowired
    private MessageRegistry   messageRegistry;
    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Override
    public boolean support(Message msgcallbackData) {
        return changenameHandler.get_namechange(msgcallbackData.getFrom().getId())!=null;
    }

    @Override
    public void handle(Message msg, TelegramLongPollingBot bot) {

        Long userId = msg.getFrom().getId();
        Changename_handler.RenameRequest request = changenameHandler.get_namechange(userId);
        Vapecompony_katalog v_k = vapecomponyKatalogRepository.findByIdAndBot_Id(request.key, request.botId).get();

        ObjectMapper mapper = new ObjectMapper();
        try {
            String entitiesJson = mapper.writeValueAsString(msg.getEntities());
            if(changenameHandler.get_namechange(msg.getFrom().getId())!=null){

                v_k.setName(msg.getText());
                v_k.setEntitiesJson(entitiesJson);
                vapecomponyKatalogRepository.save(v_k);

            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }



        changenameHandler.clear(userId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(msg.getChatId().toString());
        sendMessage.setText("✅Название изменено");
        try{
           // bot.execute(sendMessage);

            Message msgg =  bot.execute(sendMessage);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
        }
        catch (TelegramApiException e){
            e.printStackTrace();
        }

    }
}
