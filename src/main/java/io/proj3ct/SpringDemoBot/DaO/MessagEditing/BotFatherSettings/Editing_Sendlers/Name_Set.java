package io.proj3ct.SpringDemoBot.DaO.MessagEditing.BotFatherSettings.Editing_Sendlers;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.DaO.MessagEditing.BotFatherSettings.BotFatherHandlers.FatherSettings_handler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Component
public class Name_Set implements CallbackHandler {

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private FatherSettings_handler fatherSettings_handler;

    @Autowired
    private MessageRegistry messageRegistry;


    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("BotF_");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {




        String[] parts = query.getData().split(":");


        String type = parts[0];      // catalog, cart, ...
        Long botId = Long.parseLong(parts[1]);
        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        Optional<Bot> botOpt = botRepository.findById(botId);
        if (botOpt.isEmpty()) return;

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(query.getMessage().getChatId().toString());

        if(type.equals("BotF_Name")){

            sendMessage.setText("\uD83D\uDE0E Введите имя длиной до 64 символов:");
            fatherSettings_handler.expectRename(query.getFrom().getId(), botId, "Name");

        }
        else if(type.equals("BotF_About")){

            sendMessage.setText("☺\uFE0F Введите текст длиной до 120 символов:");
            fatherSettings_handler.expectRename(query.getFrom().getId(), botId, "About");

        }
        else if(type.equals("BotF_Description")){


            sendMessage.setText("\uD83D\uDE09 Введите текст,\n" +
                    "Описание не может превышать 512 символов (включая переносы строк):");
            fatherSettings_handler.expectRename(query.getFrom().getId(), botId, "Description");
        }


        //pictures

        else if(type.equals("BotF_Description_picture")){


            sendMessage.setText("\uD83D\uDCF8 Загрузите фотографию описания бота размером 640x360 пикселей. Или GIF-анимацию размером 320x180, 640x360 или 960x540 пикселей:");
            fatherSettings_handler.expectRename(query.getFrom().getId(), botId, "BotF_Description_picture");
        }

        else{

            sendMessage.setText("\uD83E\uDDD1\u200D\uD83D\uDCBB Отправьте новую аватарку:");
            fatherSettings_handler.expectRename(query.getFrom().getId(), botId, "BotF_Botpic");

        }



        try{

            //bot.execute(sendMessage);

            Message msgg =  bot.execute(sendMessage);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
        }
        catch (TelegramApiException e){
            e.printStackTrace();
        }


    }

    private String callbackDataPart(String full, String prefix) {
        return full.substring(prefix.length());
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        InlineKeyboardButton b = new InlineKeyboardButton(text);
        b.setCallbackData(callbackData);
        return b;
    }
}
