package io.proj3ct.SpringDemoBot.DaO.MessagEditing.BotFatherSettings.BotFatherEditing_Callback;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BotFather_Callback implements CallbackHandler {

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("ZMINA_BOTFATHER:");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {




        String botId = callbackDataPart(query.getData(), "ZMINA_BOTFATHER:");
        Optional<Bot> botOpt = botRepository.findById(Long.parseLong(botId));
        if (botOpt.isEmpty()) return;

        if(!cheking.mustCheck(Long.parseLong(botId), query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        EditMessageMedia sendMessage = new EditMessageMedia();

        sendMessage.setMessageId(query.getMessage().getMessageId());

        sendMessage.setChatId(query.getMessage().getChatId().toString());

        InputMediaPhoto newPhoto = new InputMediaPhoto();
        newPhoto.setParseMode("MarkDownV2");

        newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/botf.png");


        String text =  escapeMarkdown("\uD83D\uDC64")+"_*Данные профиля*_"+"\n" +
                "\n" + "> "+
                "создайте свой уникальный стиль"+escapeMarkdown("\uD83E\uDD29")+"\n\n\n"+
                escapeMarkdown("\uD83D\uDCF2")+" Настройте всё под себя"+escapeMarkdown(".");

        newPhoto.setCaption(text);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        keyboard.add(List.of(button("✏\uFE0F Редактировать имя", "BotF_Name:" + botId)));
        keyboard.add(List.of(button("\uD83D\uDCC4 Краткое Описание", "BotF_About:" + botId)));
        keyboard.add(List.of(button("\uD83D\uDCAC Стартовое сообщение", "BotF_Description:" + botId)));
        keyboard.add(List.of(button("\uD83D\uDCF8 Стартовое фото", "BotF_Description_picture:" + botId)));
        keyboard.add(List.of(button("\uD83D\uDC64 Аватарка", "BotF_Botpic:" + botId)));
        keyboard.add(List.of(button("🔙 Назад", "GO_BACK_FROM_EDIT:"+ botId)));

        sendMessage.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
        sendMessage.setMedia(newPhoto);

        try{
            bot.execute(sendMessage);


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
