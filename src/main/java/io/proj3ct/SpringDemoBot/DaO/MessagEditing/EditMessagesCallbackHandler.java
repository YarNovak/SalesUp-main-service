package io.proj3ct.SpringDemoBot.DaO.MessagEditing;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
import java.util.Optional;

@Component
public class EditMessagesCallbackHandler implements CallbackHandler {

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {

        return  ((callbackData.startsWith("ZMINA_MY_MESSAGES:")) || (callbackData.startsWith("BACK_TO_WTF_MESSAGE:")));

    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String botId;
        if(query.getData().startsWith("ZMINA_MY_MESSAGES:")) {
             botId = callbackDataPart(query.getData(), "ZMINA_MY_MESSAGES:");
        }
        else{
            botId = callbackDataPart(query.getData(), "BACK_TO_WTF_MESSAGE:");
        }

        if(!cheking.mustCheck(Long.parseLong(botId), query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        Optional<Bot> botOpt = botRepository.findById(Long.parseLong(botId));
        if (botOpt.isEmpty()) return;

        EditMessageMedia msg = new EditMessageMedia();

        msg.setMessageId(query.getMessage().getMessageId());

        msg.setChatId(query.getMessage().getChatId().toString());

        InputMediaPhoto newPhoto = new InputMediaPhoto();
        newPhoto.setParseMode("MarkDownV2");
        newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/mergedall2.png");


        String botUsername = extractUsernameFromToken(botOpt.get().getBotToken());
        String link = "https://t.me/" + botUsername + "?start";

        String string  = escapeMarkdown("\uD83D\uDCAC")+" SalesUp сообщения" + escapeMarkdown("!")+"\n" +
                "\n" +
                escapeMarkdown("❕")+"Просмотрите, как сообщения выглядят по умолчанию:\n" +
                "\n" +
                escapeMarkdown(link)+

                "\n\n" +
               escapeMarkdown("\uD83D\uDC47")+"Или выберите конкретный тип сообщений и персонализируйте их под себя";

        newPhoto.setCaption(string);
        msg.setMedia(newPhoto);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                 keyboard.add(List.of(button("\uD83D\uDC4BПриветствие", "RENAME_MESSAGE:greeting:" + botId)));

                 keyboard.add(List.of(button("\uD83D\uDD16Ассортимент", "RENAME_MESSAGE:catalog:" + botId)));

        /*
        keyboard.add(List.of(button("menu discription", "RENAME_MESSAGE:cart:" + botId)));
        keyboard.add(List.of(button("product description", "RENAME_MESSAGE:order:" + botId)));
        keyboard.add(List.of(button("adding product text", "RENAME_MESSAGE:info:" + botId)));
       */

                 keyboard.add(List.of(button("\uD83D\uDDD1Очистить корзину", "cart:"+"clearing:" + botId)));

        /*

        keyboard.add(List.of(button("cart text", "RENAME_MESSAGE:contact:" + botId)));
        keyboard.add(List.of(button("clearing text", "RENAME_MESSAGE:contact:" + botId)));
        keyboard.add(List.of(button("changing cart message", "RENAME_MESSAGE:contact:" + botId)));
        */

                  keyboard.add(List.of(button("\uD83D\uDCB8Оплата", "RENAME_MESSAGE:payment:" + botId)));

        /*
        keyboard.add(List.of(button("payment text", "RENAME_MESSAGE:contact:" + botId)));
        keyboard.add(List.of(button("phone number text", "RENAME_MESSAGE:contact:" + botId)));
        keyboard.add(List.of(button("delivery message", "RENAME_MESSAGE:contact:" + botId)));

         */

                  keyboard.add(List.of(button("\uD83D\uDCB0Обработка платежей", "RENAME_MESSAGE:payment_acception:" + botId)));

        /*

        keyboard.add(List.of(button("emoji", "RENAME_MESSAGE:contact:" + botId)));
        keyboard.add(List.of(button("after payment texts", "RENAME_MESSAGE:contact:" + botId)));


        */


                   keyboard.add(List.of(button("🔙 Назад", "GO_BACK_FROM_EDIT:" + botId)));

        msg.setReplyMarkup(new InlineKeyboardMarkup(keyboard));

        try {
            bot.execute(msg);
        } catch (TelegramApiException e) {
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
    private String extractUsernameFromToken(String token) {

        Bot need_bot = botRepository.findByBotToken(token).get();
        return need_bot.getBotusername();
    }
}
