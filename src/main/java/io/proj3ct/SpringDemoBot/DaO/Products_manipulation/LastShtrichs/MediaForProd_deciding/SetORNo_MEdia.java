package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.MediaForProd_deciding;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony_katalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class SetORNo_MEdia implements CallbackHandler {

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("SET_FORPRODMEDIA_YES") ;

    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String[] parts = query.getData().split(":");

        Long v_kid = Long.parseLong(parts[1]);
        Long botId = Long.parseLong(parts[2]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);
        Vapecompony_katalog vapecompony_katalog = vapecomponyKatalogRepository.findByIdAndBot_Id(v_kid, botId).orElse(null);

        EditMessageMedia sendMessage = new EditMessageMedia();
        sendMessage.setChatId(query.getMessage().getChatId().toString());
        sendMessage.setMessageId(query.getMessage().getMessageId());

        InputMediaPhoto inputMediaPhoto = new InputMediaPhoto("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/productDSPTV.png");
        inputMediaPhoto.setParseMode("MarkDownV2");
        inputMediaPhoto.setCaption(escapeMarkdown("\uD83C\uDF86") +"*_Редактирование медиа_*\n" +
                "\n" +
                escapeMarkdown("⚙️\uFE0F")+" модифицируйте в любой момент");

       // sendMessage.setPhoto(new InputFile("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png"));

        if(vapecompony_katalog != null) {


                if(parts[0].equals("SET_FORPRODMEDIA_YES")){

                  //  sendMessage.setCaption("Скинь що з себе представляє даний продукт");

                    List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

                    keyboard.add(List.of(button("\uD83D\uDD04 ПО УМОЛЧАНИЮ", "SET_PROD_media_DEFAULT:" + v_kid + ":" + botId)));
                    keyboard.add(List.of(button("\uD83D\uDCF8 Фото", "PRODUCTMEDIA_ADD:"+ v_kid + ":" + botId)));
                    keyboard.add(List.of(button("\uD83C\uDFAC Видео", "PRODUCTMEDIAVIDEO_ADD:"+ v_kid + ":" + botId)));
                    keyboard.add(List.of(button("\uD83D\uDD19 Назад", "AGAIN_UPDATE:"+ v_kid + ":" + botId)));

                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);
                    sendMessage.setReplyMarkup(markup);


                }
                else{
                    //sendMessage.setCaption("Продукт успышно створений, переглянь свій бот, (знизу буде хуйня тіпа ссилки на бот, чи статистика чи ше якась хуйня якої ше немає)");
                   inputMediaPhoto.setCaption("Продукт успышно створений, переглянь свій бот, (знизу буде хуйня тіпа ссилки на бот, чи статистика чи ше якась хуйня якої ше немає)");
                }


        }
        else{


          inputMediaPhoto.setCaption("Даного продукту більше не існуэ(");
        }
        sendMessage.setMedia(inputMediaPhoto);
        try{

            bot.execute(sendMessage);

         //   Message msgg =
           // sendMessage.setMedia(inputMediaPhoto);
                   // bot.execute(sendMessage);
            //messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

        }
        catch (TelegramApiException e){
            e.printStackTrace();
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
