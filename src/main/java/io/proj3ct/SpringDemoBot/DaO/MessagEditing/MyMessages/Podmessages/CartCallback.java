package io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.Podmessages;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;

import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class CartCallback implements CallbackHandler {

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("cart:");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String[] parts = query.getData().split(":");
        if (parts.length != 3) return;

        String key = parts[1];      // catalog, cart, ...
        Long botId = Long.parseLong(parts[2]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);
        // stateServise.expectButtonRename(query.getFrom().getId(), botId, key); // чекаємо вводу


        EditMessageMedia sendPhoto = new EditMessageMedia();

        sendPhoto.setMessageId(query.getMessage().getMessageId());

        sendPhoto.setChatId(query.getMessage().getChatId().toString());

        InputMediaPhoto newPhoto = new InputMediaPhoto();
        newPhoto.setParseMode("MarkDownV2");
        newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/clearcart.png");
        newPhoto.setCaption("_*Это сообщение показывается, когда клиент очищает корзину или она пуста*_"+"\n" +
                "\n" + "> "+
                escapeMarkdown("\uD83D\uDDD1")+"__Очистить корзину__ — уведомляет, что корзина пуста или заказ был очищен"+escapeMarkdown(".")+"\n\n"+
                escapeMarkdown("\uD83D\uDCA1")+" Измените текст и медиа"+escapeMarkdown("."));
        sendPhoto.setMedia(newPhoto);


        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = List.of(
                List.of(newButton("✏\uFE0F Текст", "TEXT:" +key  + ":" + botId)),
                List.of(newButton("\uD83D\uDCF8 Фото", "PHOTO:" +key  + ":" + botId)),
                List.of(newButton("\uD83C\uDFAC Відео", "VIDEO:" +key  + ":" + botId)),
                List.of(newButton("\uD83D\uDD19 Назад", "BACK_TO_WTF_MESSAGE:" + botId))
        );
        markup.setKeyboard(buttons);
        sendPhoto.setReplyMarkup(markup);


        try {
            bot.execute(sendPhoto);
        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
    private InlineKeyboardButton newButton(String text, String callbackData) {
        InlineKeyboardButton btn = new InlineKeyboardButton(text);
        btn.setCallbackData(callbackData);
        return btn;
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
