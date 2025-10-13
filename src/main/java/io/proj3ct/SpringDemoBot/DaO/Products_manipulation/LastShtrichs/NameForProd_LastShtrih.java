package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddCena_prod_handler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.Add_prod_handler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony_katalog;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class NameForProd_LastShtrih implements MessageHandle {

    @Autowired
    private Add_prod_handler add_prod_handler;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private AddCena_prod_handler addCenaProdHandler;

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;
    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(org.telegram.telegrambots.meta.api.objects.Message msg) {

        return (add_prod_handler.get_new_product(msg.getFrom().getId()) != null );

    }

    @Override
    public void handle(Message msg, TelegramLongPollingBot bot) {

        Vapecompony_katalog v_k = new Vapecompony_katalog();
        v_k.setName(msg.getText());
        v_k.setBot(botRepository.findById( add_prod_handler.get_new_product(msg.getFrom().getId())).get());
       // v_k.setVapecompony(vapecomponyRepository.findByIdAndBot_Id(1L, 2L).get()); //!!!!

        System.out.println("OMG " + v_k.getName() + " "+ v_k.getBot().getId());

        ObjectMapper mapper = new ObjectMapper();
        try {
            String entitiesJson = mapper.writeValueAsString(msg.getEntities());
            if(add_prod_handler.get_new_product(msg.getFrom().getId())!=null){

                v_k.setName(msg.getText());
                v_k.setEntitiesJson(entitiesJson);
                vapecomponyKatalogRepository.save(v_k);

            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }




        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(msg.getChatId().toString());
        sendMessage.setText("\uD83D\uDCB8 Укажите стоимость:");

        addCenaProdHandler.expect_cena_product(msg.getFrom().getId(), add_prod_handler.get_new_product(msg.getFrom().getId()), v_k.getName());
        add_prod_handler.clear(msg.getFrom().getId());

        try {
            //  bot.execute(sendMessage);

            Message msgg =  bot.execute(sendMessage);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }
}
