package io.proj3ct.SpringDemoBot.DaO.MenuManaging.PodMenu_changeszczegoly;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Description.MenuDescription_handler;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Names.MenuName_handler;

import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class MenuChange implements CallbackHandler {

    @Autowired
    private MenuName_handler menuName_handler;

    @Autowired
    private MenuDescription_handler menuDescription_handler;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;



    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("CHANGE_PODMENU_");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String[] parts = query.getData().split(":");

        Long vapecompony_id = Long.parseLong(parts[1]);
        Long botId = Long.parseLong(parts[2]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        EditMessageMedia sendMessage = new EditMessageMedia();
        sendMessage.setMessageId(query.getMessage().getMessageId());

        sendMessage.setChatId(query.getMessage().getChatId().toString());

        InputMediaPhoto newPhoto = new InputMediaPhoto();
        newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/menuDST.png");

        if(parts[0].equals("CHANGE_PODMENU_NAME")){

            SendMessage sendMessag = new SendMessage();
            sendMessag.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendMessag.setText("\uD83D\uDCAC Введите новый текст:");

           // sendMessage.setMedia(newPhoto);

            menuName_handler.expectNameRename(query.getFrom().getId(), botId, vapecompony_id);
            try {
             //   bot.execute(sendMessag);

                Message msgg =  bot.execute(sendMessag);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }


        }
        else if(parts[0].equals("CHANGE_PODMENU_DISCRIPTION")){

         //   SendMessage sendMessage = new SendMessage();
         //   sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));
        //    sendMessage.setText("Введи своє бачення даної менюшки:");
            newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/menuDSPTV.png");
            newPhoto.setCaption(escapeMarkdown("✏\uFE0F") +"*_Редактирование описания_*\n" +
                    "\n" +
                    escapeMarkdown("⚙\uFE0F")+" модифицируйте в любой момент");
            newPhoto.setParseMode("MarkDownV2");
            sendMessage.setMedia(newPhoto);

            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            keyboard.add(List.of(button("\uD83D\uDD04 ПО УМОЛЧАНИЮ", "SET_VAPEDESC_DEFAULT:" + vapecompony_id + ":" + botId)));
            keyboard.add(List.of(button("\uD83D\uDCDDИзменить", "SET_VAPEDESC_CHNG:"+ vapecompony_id + ":" + botId)));
            keyboard.add(List.of(button("\uD83D\uDD19 Назад", "EDIT_PODMENU:" + vapecompony_id+":" + botId)));

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);
            sendMessage.setReplyMarkup(markup);
            try {
                bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }
        else if(parts[0].equals("CHANGE_PODMENU_MEDIA")){

        //    SendMessage sendMessage = new SendMessage();
        //    sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));
        //    sendMessage.setText("Скинь що з себе представляє даний продукт");

            newPhoto.setParseMode("MarkDownV2");
            newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/menuDST.png");
            newPhoto.setCaption(escapeMarkdown("\uD83C\uDF86") +"*_Редактирование медиа_*\n" +
                    "\n" +
                    escapeMarkdown("⚙️\uFE0F")+" модифицируйте в любой момент");
            sendMessage.setMedia(newPhoto);


            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

            keyboard.add(List.of(button("\uD83D\uDD04 ПО УМОЛЧАНИЮ", "SET_VAPEMEDIA_DEFAULT:" + vapecompony_id + ":" + botId)));
            keyboard.add(List.of(button("\uD83D\uDCF8 Фото", "VAPEMEDIA_ADD:"+ vapecompony_id + ":" + botId)));
            keyboard.add(List.of(button("\uD83C\uDFAC Видео", "VIDEOVAPEMEDIA_ADD:"+ vapecompony_id + ":" + botId)));
            keyboard.add(List.of(button("\uD83D\uDD19 Назад", "EDIT_PODMENU:" + vapecompony_id+":" + botId)));

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);
            sendMessage.setReplyMarkup(markup);

            //  menuDescription_handler.expectDescriptionRename(query.getFrom().getId(), botId, vapecompony_id);
            try {
                bot.execute(sendMessage);
            }
            catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }


        }



    }

    private InlineKeyboardButton button(String text, String key) {

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(key);
        return button;
    }
    private String escapeMarkdown(String text) {
        return text.replace("\\", "\\\\")  // Екрануємо зворотні слеші
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
    }
}
