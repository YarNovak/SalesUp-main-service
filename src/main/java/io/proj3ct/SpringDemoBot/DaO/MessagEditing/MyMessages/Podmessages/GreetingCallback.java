package io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.Podmessages;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import org.checkerframework.checker.units.qual.A;
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
public class GreetingCallback implements CallbackHandler {

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("greeting:");
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


        if(key.equals("greeting")){

            newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/start1TPPV.png");
            newPhoto.setParseMode("MarkDownV2");
            newPhoto.setCaption(escapeMarkdown("\uD83C\uDF89")+"_*Вступление*_\n" +
                    "\n" +
                    "> "+escapeMarkdown("\uD83C\uDF1F")+"Сделайте вступление живым и индивидуальным для ваших клиентов"+escapeMarkdown("!") +
                     "\n\n" +
                    escapeMarkdown("⚙\uFE0F")+ "Измените сообщение"+escapeMarkdown("\uD83D\uDC47"));
            sendPhoto.setMedia(newPhoto);


            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> buttons = List.of(
                    List.of(newButton("✏\uFE0F Текст", "TEXT:" +key  + ":" + botId)),
                    List.of(newButton("\uD83D\uDCF8 Фото", "PHOTO:" +key  + ":" + botId)),
                    List.of(newButton("\uD83C\uDFAC Видео", "VIDEO:" +key  + ":" + botId)),
                    List.of(newButton("🔙 Назад", "RENAME_MESSAGE:" +parts[0]  + ":" + botId))
            );

            System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP " + key );
            markup.setKeyboard(buttons);
            sendPhoto.setReplyMarkup(markup);

        }
        else{
            newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/start2TPV.png");
            newPhoto.setParseMode("MarkDownV2");
            newPhoto.setCaption(escapeMarkdown("\uD83D\uDCBC")+"_*Знакомство с сервисом*_\n" +
                    "\n" +
                    "> "+escapeMarkdown("\uD83C\uDF1F")+"Покажите клиентам, чем именно вы отличаетесь"+escapeMarkdown("!") +
                    "\n\n" +
                    escapeMarkdown("⚙\uFE0F")+ "Измените сообщение"+escapeMarkdown("\uD83D\uDC47"));
            sendPhoto.setMedia(newPhoto);


            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> buttons = List.of(
                    List.of(newButton("✏\uFE0F Текст", "TEXT:" +key  + ":" + botId)),
                    List.of(newButton("\uD83D\uDCF8 Фото", "PHOTO:" +key  + ":" + botId)),
                    List.of(newButton("\uD83C\uDFAC Видео", "VIDEO:" +key  + ":" + botId)),
                    List.of(newButton("🔙 Назад", "RENAME_MESSAGE:" +parts[0]  + ":" + botId))
            );

            System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP " + key );
            markup.setKeyboard(buttons);
            sendPhoto.setReplyMarkup(markup);

        }


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
