package io.proj3ct.SpringDemoBot.DaO.MessagEditing;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;

import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class RenameMessages_First_CallbackHandler implements CallbackHandler {

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;


    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("RENAME_MESSAGE:");
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
        EditMessageMedia msg = new EditMessageMedia();

        msg.setMessageId(query.getMessage().getMessageId());

        msg.setChatId(query.getMessage().getChatId().toString());

        InputMediaPhoto newPhoto = new InputMediaPhoto();
      //  https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/menuDST.png
        newPhoto.setParseMode("MarkDownV2");
        newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/main/start12.jpg");
        newPhoto.setCaption("вот так вот)");



        if(key.equals("greeting")){
/*
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(query.getMessage().getChatId().toString());
            sendPhoto.setCaption("ULIAAA");
            sendPhoto.setPhoto(new InputFile("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png"));

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> buttons = List.of(
                    List.of(newButton("📄 Текст", "TEXT:" +key  + ":" + botId)),
                    List.of(newButton("🖼 Фото", "PHOTO:" +key  + ":" + botId)),
                    List.of(newButton("🎥 Відео", "VIDEO:" +key  + ":" + botId))
            );
            markup.setKeyboard(buttons);
            sendPhoto.setReplyMarkup(markup);






            try {
                bot.execute(sendPhoto);
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }

            //stateServise.expectButtonRename(query.getFrom().getId(), botId, key);
            return;

 */
            newPhoto.setCaption("_*В SalesUp первые два сообщения очень важны*_"+"\n" +
                    "\n" + "> "+
                    escapeMarkdown("🎉")+"__Вступление__ — приветствие пользователя"+escapeMarkdown(".")+"\n"+ "> "+"\n"+"> "+
                   escapeMarkdown("\uD83D\uDCBC")+"__Знакомство с сервисом__ — кратко о возможностях"+escapeMarkdown("."));

            // newPhoto.setMedia("https://i.postimg.cc/TPh5LPtg/start12.jpg");
            newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/main/start12.jpg");
            keyboard.add(List.of(button("\uD83C\uDF89Вступление", "greeting:greeting:" + botId)));
            keyboard.add(List.of(button("\uD83D\uDCBCЗнакомство с сервисом", "greeting:menu:" + botId)));
            keyboard.add(List.of(button("🔙 Назад", "BACK_TO_WTF_MESSAGE:" + botId)));


        }
        else if(key.equals("catalog")){


            newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/AsortimentPTV.png");
            newPhoto.setCaption("_*Это сообщение отображается при нажатии на Каталог*_"+"\n" +
                    "\n" + "> "+
                    escapeMarkdown("\uD83D\uDD16")+"__Ассортимент__ — показывает товары или услуги, которые предлагает ваш бизнес"+escapeMarkdown(".")+"\n\n"+
                    escapeMarkdown("\uD83D\uDCA1")+" Измените текст и медиа"+escapeMarkdown("."));


            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> buttons = List.of(
                    List.of(newButton("✏\uFE0F Текст", "TEXT:" +key  + ":" + botId)),
                    List.of(newButton("\uD83D\uDCF8 Фото", "PHOTO:" +key  + ":" + botId)),
                    List.of(newButton("\uD83C\uDFAC Відео", "VIDEO:" +key  + ":" + botId)),
                    List.of(button("🔙 Назад", "BACK_TO_WTF_MESSAGE:" + botId))
            );
            markup.setKeyboard(buttons);
            msg.setReplyMarkup(markup);
            msg.setMedia(newPhoto);


            try {
                bot.execute(msg);
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }

            //stateServise.expectButtonRename(query.getFrom().getId(), botId, key);
            return;

        }
        else if(key.equals("cart")){

            newPhoto.setCaption("_*Это сообщение показывается, когда клиент очищает корзину или она пуста*_"+"\n" +
                    "\n" + "> "+
                    escapeMarkdown("\uD83D\uDDD1")+"__Очистить корзину__ — уведомляет, что корзина пуста или заказ был очищен"+escapeMarkdown(".")+"\n\n"+
                    escapeMarkdown("\uD83D\uDCA1")+" Измените текст и медиа"+escapeMarkdown("."));

            newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/clearcart.png");

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> buttons = List.of(
                    List.of(newButton("✏\uFE0F Текст", "TEXT:" +"clearing"  + ":" + botId)),
                    List.of(newButton("\uD83D\uDCF8 Фото", "PHOTO:" +"clearing" + ":" + botId)),
                    List.of(newButton("\uD83C\uDFAC Відео", "VIDEO:" +"clearing"  + ":" + botId)),
                    List.of(button("🔙 Назад", "BACK_TO_WTF_MESSAGE:" + botId))
            );
            markup.setKeyboard(buttons);
            msg.setReplyMarkup(markup);
            msg.setMedia(newPhoto);


            try {
                bot.execute(msg);
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }

            //stateServise.expectButtonRename(query.getFrom().getId(), botId, key);
            return;
        }

        else if(key.equals("please_whait")){

            newPhoto.setCaption("_*Это сообщение отображается при нажатии на Каталог*_"+"\n" +
                    "\n" + "> "+
                    escapeMarkdown("\uD83D\uDD16")+"__Ассортимент__ — показывает товары или услуги, которые предлагает ваш бизнес"+escapeMarkdown(".")+"\n\n"+
                    escapeMarkdown("\uD83D\uDCA1")+" Измените текст и медиа"+escapeMarkdown("."));

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> buttons = List.of(
                    List.of(newButton("✏\uFE0F Текст", "TEXT:" +key  + ":" + botId)),
                    List.of(newButton("\uD83D\uDCF8 Фото", "PHOTO:" +key  + ":" + botId)),
                    List.of(newButton("\uD83C\uDFAC Відео", "VIDEO:" +key  + ":" + botId)),
                    List.of(button("🔙 Назад", "BACK_TO_WTF_MESSAGE:" + botId))
            );
            markup.setKeyboard(buttons);
            msg.setReplyMarkup(markup);
            msg.setMedia(newPhoto);


            try {
                bot.execute(msg);
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }

            //stateServise.expectButtonRename(query.getFrom().getId(), botId, key);
            return;

        }

        else if(key.equals("payment")){

            newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/forpayment.png");
            newPhoto.setCaption("_*Полностью адаптируйте процесс оплаты под ваши нужды*_"+"\n" +
                    "\n" + "> "+
                    escapeMarkdown("\uD83C\uDFE6")+"__Изменить оплату__ — настройте методы оплаты"+escapeMarkdown(".")+"\n"+ "> "+"\n"+"> "+
                    escapeMarkdown("\uD83D\uDCF1")+"__Отправить телефон__ — клиент отправляет контакт связи"+escapeMarkdown(".")+"\n"+ "> "+"\n"+"> "+
                    escapeMarkdown("\uD83D\uDCF2")+"__Телефон получен__ — подтверждение, что контакт получен"+escapeMarkdown(".")+"\n"+ "> "+"\n"+"> "+
                    escapeMarkdown("\uD83D\uDCCD")+"__Адрес для доставки__ — подтверждение, что адрес получен"+escapeMarkdown("."));

            keyboard.add(List.of(button("\uD83C\uDFE6 Изменить оплату", "payment:payment_methods:" + botId)));
            keyboard.add(List.of(button("\uD83D\uDCF1 Отправить телефон", "payment:phone:" + botId)));
            keyboard.add(List.of(button("\uD83D\uDCF2 Телефон получен", "payment:phone_thanks:" + botId)));
            keyboard.add(List.of(button("\uD83D\uDCCD Адрес для доставки", "payment:delivery:" + botId)));

            keyboard.add(List.of(button("🔙 Назад", "BACK_TO_WTF_MESSAGE:" + botId)));
        }
        else if(key.equals("payment_acception")){

            newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/allaccdone.png");
            newPhoto.setCaption("_*SalesUp дает возможность изменять тексты сообщений о состоянии заказа*_"+"\n" +
                    "\n" + "> "+
                    escapeMarkdown("✅")+"   __Заказ оформлен__"+"\n"+ "> "+"\n"+"> "+
                    escapeMarkdown("\uD83D\uDFE2")+"   __Подтверждено__"+"\n"+ "> "+"\n"+"> "+
                    escapeMarkdown("❌")+"   __Отклонено__"+"\n\n"+
                    escapeMarkdown("\uD83D\uDCA1")+" Настройте их под свой стиль"+escapeMarkdown("."));

            keyboard.add(List.of(button("✅ Заказ оформлен", "payment_acception:congrat:" + botId)));
            keyboard.add(List.of(button("\uD83D\uDFE2 Подтверждено", "payment_acception:accept:" + botId)));
            keyboard.add(List.of(button("❌ Отклонено", "payment_acception:deny:" + botId)));
            keyboard.add(List.of(button("🔙 Назад", "BACK_TO_WTF_MESSAGE:" + botId)));
        }
        msg.setMedia(newPhoto);
        msg.setReplyMarkup(new InlineKeyboardMarkup(keyboard));

        try{
            bot.execute(msg);
        }
        catch(TelegramApiException e){
            e.printStackTrace();
        }
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        InlineKeyboardButton b = new InlineKeyboardButton(text);
        b.setCallbackData(callbackData);
        return b;
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



