package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.Add_Prod;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.Add_prod_handler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class AddProduct implements CallbackHandler {

    @Autowired
    private Add_prod_handler addProdHandler;

    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("PRODUCT_ADD:");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {




        String[] parts = query.getData().split(":");
        Long botId = Long.parseLong(parts[1]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));
        if(vapecomponyRepository.findAllByBot_Id(botId).isEmpty())
        {
            sendMessage.setText("\uD83D\uDE09 Создай сначала раздел где будет находиться твой будущий элемент");

            try{

              //  bot.execute(sendMessage);

                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
            }
            catch (TelegramApiException e){

                e.printStackTrace();

            }
            return;
        }
        sendMessage.setText("✏\uFE0F Введите название нового продукта или услуги:");
        addProdHandler.expect_new_product(query.getFrom().getId(), botId);



        try{

          //  bot.execute(sendMessage);
            Message msgg =  bot.execute(sendMessage);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
        }
        catch(TelegramApiException e){
            e.printStackTrace();
        }


    }
}
