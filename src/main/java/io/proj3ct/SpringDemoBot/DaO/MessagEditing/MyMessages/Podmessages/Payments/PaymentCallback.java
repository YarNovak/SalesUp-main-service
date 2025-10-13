package io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.Podmessages.Payments;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;

import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.repository.BotMessageRepository;
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
public class PaymentCallback implements CallbackHandler {




    @Autowired
    private BotMessageRepository botMessageRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("payment:");
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
        newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/Please_sendPhone.png");
        newPhoto.setParseMode("MarkDownV2");
        newPhoto.setCaption(escapeMarkdown("\uD83D\uDCB0") +"*_Варианты оплаты_*\n" +
                "\n" +
               "> "+escapeMarkdown("\uD83D\uDCB8")+"__Наличные__ — удобно оплатить заказ при получении\n" +
                "> "+"\n" +
                "> "+escapeMarkdown("\uD83C\uDF10")+"__Онлайн__  — карты, криптовалюта, переводы и всевозможные методы\n" +
                "\n" +
                escapeMarkdown("⚙\uFE0F")+" Методы можно добавлять, изменять или удалять");
        sendPhoto.setMedia(newPhoto);





        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        if(key.equals("payment_methods")){


            newPhoto.setMedia("https://github.com/YarNovak/BOT12500Photos/blob/main/Oplata.jpg?raw=true");
            List<List<InlineKeyboardButton>> buttons = List.of(
                    List.of(newButton("\uD83D\uDCB0Наличные", "Nalichka:" + botId)),
                    List.of(newButton("\uD83C\uDF10 Онлайн-оплата", "Karta:" + botId)),
                    List.of(newButton("🔙 Назад", "RENAME_MESSAGE:" +parts[0]  + ":" + botId))
            );
            markup.setKeyboard(buttons);

        }
        else if(key.equals("phone")){

            newPhoto.setCaption(escapeMarkdown("\uD83D\uDCF2")+ "_*Отправить конакт*_\n" +
                    "\n" + "> "+
                    escapeMarkdown("\uD83D\uDC64")+"Позволяет быстро поделиться контактом — удобно для связи и обработки заказов"+escapeMarkdown("👌")+"\n" +
                    "\n" +
                    escapeMarkdown("\uD83D\uDCA1")+" Измените текст и медиа"+escapeMarkdown("."));

            newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/phone_askPTV.png");

            List<List<InlineKeyboardButton>> buttons = List.of(
                    List.of(newButton("✏\uFE0F Текст", "TEXT:" +key  + ":" + botId)),
                    List.of(newButton("\uD83D\uDCF8 Фото", "PHOTO:" +key  + ":" + botId)),
                    List.of(newButton("🎥 Відео", "VIDEO:" +key  + ":" + botId)),
                    List.of(newButton("🔙 Назад", "RENAME_MESSAGE:" +parts[0]  + ":" + botId)));
            markup.setKeyboard(buttons);

        }
        else if(key.equals("phone_thanks")){

            newPhoto.setCaption(escapeMarkdown("\uD83D\uDCF1")+ "_*Контакт получен*_\n" +
                    "\n" + "> "+
                    escapeMarkdown("\uD83D\uDC64")+"Сообщение о успешном сохранении контакта клиента для будущей коммуникации"+escapeMarkdown("\uD83D\uDE0E")+"\n" +
                    "\n" +
                    escapeMarkdown("\uD83D\uDCA1")+" Измените текст и медиа"+escapeMarkdown("."));

            newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/thanks_phone.png");

                List<List<InlineKeyboardButton>> buttons = List.of(
                        List.of(newButton("✏\uFE0F Текст", "TEXT:" +key  + ":" + botId)),
                        List.of(newButton("\uD83D\uDCF8 Фото", "PHOTO:" +key  + ":" + botId)),
                        List.of(newButton("🎥 Відео", "VIDEO:" +key  + ":" + botId)),
                        List.of(newButton("🔙 Назад", "RENAME_MESSAGE:" +parts[0]  + ":" + botId)));
                markup.setKeyboard(buttons);
        }

        else if(key.equals("delivery")){

            newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/AdresPTV.png");
            newPhoto.setCaption(escapeMarkdown("\uD83D\uDCCD")+ "_*Адрес для доставки*_\n" +
                    "\n" + "> "+
                    escapeMarkdown("\uD83D\uDC64")+"Сообщение которое призывает отослать адрес для доставки заказа"+escapeMarkdown("\uD83D\uDC4C")+"\n" +
                    "\n" +
                    escapeMarkdown("\uD83D\uDCA1")+" Измените текст и медиа"+escapeMarkdown("."));
            List<List<InlineKeyboardButton>> buttons = List.of(
                    List.of(newButton("✏\uFE0F Текст", "TEXT:" +key  + ":" + botId)),
                    List.of(newButton("\uD83D\uDCF8 Фото", "PHOTO:" +key  + ":" + botId)),
                    List.of(newButton("🎥 Відео", "VIDEO:" +key  + ":" + botId)),
                    List.of(newButton("🔙 Назад", "RENAME_MESSAGE:" +parts[0]  + ":" + botId)));
            markup.setKeyboard(buttons);

        }
        else if(key.equals("payment_ask") || key.equals("sendmoney_ask"))
        {
            List<List<InlineKeyboardButton>> buttons = List.of(
                    List.of(newButton("✏\uFE0F Текст", "TEXT:" +key  + ":" + botId)),
                    List.of(newButton("\uD83D\uDCF8 Фото", "PHOTO:" +key  + ":" + botId)),
                    List.of(newButton("🎥 Відео", "VIDEO:" +key  + ":" + botId)),
                    List.of(newButton("🔙 Назад", "Karta:"+ botId)));
            markup.setKeyboard(buttons);

        }
        else{


                List<List<InlineKeyboardButton>> buttons = List.of(
                List.of(newButton("✏\uFE0F Текст", "TEXT:" +key  + ":" + botId)),
                List.of(newButton("\uD83D\uDCF8 Фото", "PHOTO:" +key  + ":" + botId)),
                List.of(newButton("\uD83C\uDFAC Відео", "VIDEO:" +key  + ":" + botId)),
                        List.of(newButton("🔙 Назад", "RENAME_MESSAGE:" +parts[0]  + ":" + botId))
            );
            markup.setKeyboard(buttons);
        }

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

