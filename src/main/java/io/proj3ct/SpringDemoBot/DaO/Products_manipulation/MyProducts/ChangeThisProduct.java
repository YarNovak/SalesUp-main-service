package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.MyProducts;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony_katalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChangeThisProduct implements CallbackHandler {

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return (callbackData.startsWith("LETS_UPDATE:") || callbackData.startsWith("AGAIN_UPDATE:"));
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String[] parts = query.getData().split(":");
        Long v_kid = Long.parseLong(parts[1]);
        Long bot_id = Long.parseLong(parts[2]);

        if(!cheking.mustCheck(bot_id, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        if(query.getData().startsWith("LETS_UPDATE:")){
            SendPhoto sendMessage = new SendPhoto();
            //  sendMessage.setMessageId(query.getMessage().getMessageId());
            sendMessage.setParseMode("MarkDownV2");
            sendMessage.setChatId(query.getMessage().getChatId().toString());
            sendMessage.setPhoto(new InputFile("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/ProductPTV.png"));

            Vapecompony_katalog vapecompony_katalog = vapecomponyKatalogRepository.findByIdAndBot_Id(v_kid, bot_id).orElse(null);

            String text = (escapeMarkdown("⚙\uFE0F")+" Теперь измените продукт\n" +
                    "\n" +
                    escapeMarkdown("✏\uFE0F")+" _Выберите опцию ниже_"+ escapeMarkdown("\uD83D\uDC47"));

            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            keyboard.add(List.of(button("✏\uFE0F Название", "UPDATE_FORPROD_NAME:" + v_kid + ":" + bot_id)));
            keyboard.add(List.of(button("\uD83E\uDD11 Цена", "UPDATE_FORPROD_COST:" + v_kid + ":" + bot_id)));
            keyboard.add(List.of(button("\uD83D\uDCCA Количество", "UPDATE_FORPROD_COUNT:" + v_kid + ":" + bot_id)));
            keyboard.add(List.of(button("\uD83D\uDECD\uFE0F Раздел", "UPDATE_FORPROD_PODMENU:" + v_kid + ":" + bot_id)));
            keyboard.add(List.of(button("📝 Описание", "UPDATE_FORPROD_DESCRIPTION:"+ v_kid + ":" + bot_id)));
            keyboard.add(List.of(button("\uD83C\uDFAC Медиа", "SET_FORPRODMEDIA_YES:"+ v_kid + ":" + bot_id)));
            keyboard.add(List.of(button("❌ УДАЛИТЬ", "DELETE_PRODUCT:"+ v_kid + ":" + bot_id)));
            keyboard.add(List.of(button("\uD83D\uDD19 Назад", "TOTALLY_END")));



            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);


            sendMessage .setChatId(query.getMessage().getChatId().toString());
            sendMessage.setParseMode("MarkDownV2");
            sendMessage.setCaption(escapeMarkdown("✏\uFE0F") +"*_Редактирование продукта_*\n" +
                    "\n" +
                    escapeMarkdown("⚙\uFE0F")+" модифицируйте в любой момент");
            sendMessage.setReplyMarkup(markup);

            try{
                bot.execute(sendMessage);
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else{
            EditMessageMedia sendMessage = new EditMessageMedia();

            sendMessage.setMessageId(query.getMessage().getMessageId());
            sendMessage.setChatId(query.getMessage().getChatId().toString());

            Vapecompony_katalog vapecompony_katalog = vapecomponyKatalogRepository.findByIdAndBot_Id(v_kid, bot_id).orElse(null);
            String text = (escapeMarkdown("⚙\uFE0F")+" Теперь измените продукт\n" +
                    "\n" +
                    escapeMarkdown("✏\uFE0F")+" _Выберите опцию ниже_"+ escapeMarkdown("\uD83D\uDC47"));

            InputMediaPhoto inputMediaPhoto = new InputMediaPhoto("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/ProductPTV.png");
           inputMediaPhoto.setParseMode("MarkDownV2");
            inputMediaPhoto.setCaption(text);


            //sendMessage.setPhoto(new InputFile("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png"));





            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            keyboard.add(List.of(button("✏\uFE0F Название", "UPDATE_FORPROD_NAME:" + v_kid + ":" + bot_id)));
            keyboard.add(List.of(button("\uD83E\uDD11 Цена", "UPDATE_FORPROD_COST:" + v_kid + ":" + bot_id)));
            keyboard.add(List.of(button("\uD83D\uDCCA Количество", "UPDATE_FORPROD_COUNT:" + v_kid + ":" + bot_id)));
            keyboard.add(List.of(button("\uD83D\uDECD\uFE0F Раздел", "UPDATE_FORPROD_PODMENU:" + v_kid + ":" + bot_id)));
            keyboard.add(List.of(button("📝 Описание", "UPDATE_FORPROD_DESCRIPTION:"+ v_kid + ":" + bot_id)));
            keyboard.add(List.of(button("\uD83C\uDFAC Медиа", "SET_FORPRODMEDIA_YES:"+ v_kid + ":" + bot_id)));
            keyboard.add(List.of(button("❌ УДАЛИТЬ", "DELETE_PRODUCT:"+ v_kid + ":" + bot_id)));
            keyboard.add(List.of(button("\uD83D\uDD19 Назад", "TOTALLY_END")));


            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);


            sendMessage .setChatId(query.getMessage().getChatId().toString());
            sendMessage.setMedia(inputMediaPhoto);
            sendMessage.setReplyMarkup(markup);

            try{
                bot.execute(sendMessage);
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
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
