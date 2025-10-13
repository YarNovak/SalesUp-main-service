package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.Change_Prod.Opys_Zaibal;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.Change_Prod.NewLastShtrihs.ChangeOpys_handler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony_katalog;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Opys_Deciding implements CallbackHandler {

    @Autowired
    private ChangeOpys_handler changeOpysHandler;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;


    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("SET_PRODDESC_");

    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String[] parts = query.getData().split(":");

        Long v_kid = Long.parseLong(parts[1]);
        Long botId = Long.parseLong(parts[2]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(query.getMessage().getChatId().toString());


        if(parts[0].equals("SET_PRODDESC_DEFAULT")){

            Vapecompony_katalog vapecompony_katalog = vapecomponyKatalogRepository.findByIdAndBot_Id(v_kid, botId).orElse(null);

            vapecompony_katalog.setDescription(null);
            vapecompony_katalog.setEntitiesDescJson(null);

            vapecomponyKatalogRepository.save(vapecompony_katalog);

            sendMessage.setText("✅ Описание теперь отсутствует");

        }
        else if(parts[0].equals("SET_PRODDESC_CHNG")){

            changeOpysHandler.expect_opyschange_product(query.getFrom().getId(), botId,  v_kid);
            sendMessage.setText("✏\uFE0FОпишите своё видение этого елемента:");

        }
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
