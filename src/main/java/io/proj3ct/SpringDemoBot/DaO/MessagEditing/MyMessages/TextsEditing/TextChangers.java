package io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.TextsEditing;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.BotMessage;
import io.proj3ct.SpringDemoBot.DB_entities.BotMessageTextsDef;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.PhotoEditing.MStateServise;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.repository.BotDefTextRepository;
import io.proj3ct.SpringDemoBot.repository.BotMessageRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class TextChangers  implements CallbackHandler {

    @Autowired
    private BotMessageRepository botMessageRepository;

    @Autowired
    private MStateServise mStateServise;

    @Autowired
    private BotRepository botRepository;;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private BotDefTextRepository botDefTextRepository;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("TEXT_");
    }



    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        String[] parts = query.getData().split(":");
        if (parts.length != 3) {return;}

        String key = parts[1];      // catalog, cart, ...
        Long botId = Long.parseLong(parts[2]);


        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        if(parts[0].equals("TEXT_DEFAULT")){

            /*
            EditMessageMedia sendMessage = new EditMessageMedia();
            sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendMessage.setMessageId(query.getMessage().getMessageId());

            InputMediaPhoto newPhoto = new InputMediaPhoto();
            newPhoto.setMedia("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png");
            newPhoto.setCaption("I've set fuck for you in order you can be easily fucked up by that beatch");
*/


            BotMessage botMessage = botMessageRepository.findByMessageKeyAndBot_Id(key, botId).get();


            SendMessage sendMessage = new SendMessage();

            sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendMessage.setText("✅ Текст успешно обновлен");

            BotMessageTextsDef btt = botDefTextRepository.findByMessageKey(key).get();
            //  System.out.println( "KAKA"+btt.getMessageKey());

            botMessage.setText(btt.getText());
            botMessage.setEntitiesJson(btt.getEntitiesJson());


            Bot clientBot = botRepository.findById(botId).get();

            clientBot.setActive(false);
            botRepository.save(clientBot);

            botMessageRepository.save(botMessage);

            try {

               Message msg =  bot.execute(sendMessage);
               messageRegistry.addMessage(msg.getChatId(), msg.getMessageId());

            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }
        else if(parts[0].equals("TEXT_CHANGE")){

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));

            BotMessage bm = botMessageRepository.findByMessageKeyAndBot_Id(key, botId) .orElseThrow(() -> new RuntimeException("Медіа не знайдено"));
            
            sendMessage.setText("\uD83D\uDCAC Введите новый текст:");



            mStateServise.expectTextRename(query.getFrom().getId(), botId, key);

            try{

                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

              //  bot.execute(sendMessage);

            }
            catch(TelegramApiException e) {
                e.printStackTrace();
            }

        }

    }

    private InlineKeyboardButton newButton(String text, String callbackData) {
        InlineKeyboardButton btn = new InlineKeyboardButton(text);
        btn.setCallbackData(callbackData);
        return btn;
    }

}
