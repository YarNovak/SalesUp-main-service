package io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.Podmessages.Payments.Add_deleteMethods;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.Config.BotConfig;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class Nalichka_method implements CallbackHandler {

   @Autowired
   private BotRepository botRepository;

   @Autowired
   private MessageRegistry messageRegistry;

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {

        return (callbackData.startsWith("Nalichka:") ||(callbackData.startsWith("Karta:"))) ;
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String[] parts = query.getData().split(":");

        String key = parts[0];      // catalog, cart, ...
        Long botId = Long.parseLong(parts[1]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);
        Bot botik = botRepository.findById(botId).get();

        EditMessageMedia sendPhoto = new EditMessageMedia();
        sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));
        sendPhoto.setMessageId(query.getMessage().getMessageId());

        InputMediaVideo newPhoto = new InputMediaVideo();
        newPhoto.setMedia("https://yarnovak.github.io/BOT12500Photos/screenrecording-09-13-2025-14-17-24-1_65X0V68g.mp4");
        newPhoto.setParseMode("MarkDownV2");
        newPhoto.setCaption("ось такі є:");
        sendPhoto.setMedia(newPhoto);


       // sendPhoto.setCaption("ULIAAA");
       // sendPhoto.setPhoto(new InputFile("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png"));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        if(key.equals("Nalichka")){

            newPhoto.setCaption(escapeMarkdown("\uD83D\uDCB0") +"*_Наличные_*\n" +
                    "\n" +
                    "> "+escapeMarkdown("\uD83D\uDCB8")+"Оплата при получении заказа — удобно и просто"+escapeMarkdown(".")+ "Подходит для самовывоза и доставки"+escapeMarkdown("\uD83D\uDC4C") );


            if(botik.isNalichka()){

                List<List<InlineKeyboardButton>> buttons = List.of(
                       // List.of(newButton("📄 Текст", "TEXT:" +key  + ":" + botId)),
                    //    List.of(newButton("🖼 Фото", "PHOTO:" +key  + ":" + botId)),
                    //    List.of(newButton("🎥 Відео", "VIDEO:" +key  + ":" + botId)),
                        List.of(newButton("❌ УДАЛИТЬ МЕТОД", "DELETE_METHOD:" +key  + ":" + botId)),
                        List.of(newButton("🔙 Назад", "payment:" +"payment_methods"  + ":" + botId)));

                markup.setKeyboard(buttons);

            }
            else{

                List<List<InlineKeyboardButton>> buttons = List.of(
                        List.of(newButton("\uD83D\uDCB5 Добавить метод", "ADD_METHOD:" +key  + ":" + botId)),
                        List.of(newButton("🔙 Назад", "payment:" +"payment_methods"  + ":" + botId)));;
                markup.setKeyboard(buttons);

            }
            sendPhoto.setReplyMarkup(markup);
        }
        else{

            String txt = escapeMarkdown("\uD83C\uDF10")+ "_*Онлайн оплата*_\n" +
                    "\n" +
                    "> Карты, переводы, криптовалюту и другие доступные методы — удобно и универсально для всех клиентов"+escapeMarkdown("\uD83D\uDC4C") +"\n"+
                    "\n" +
                    escapeMarkdown("\uD83D\uDCA1") +"Измените текст и медиа";

            newPhoto.setMedia("https://yarnovak.github.io/BOT12500Photos/docs/cart.mp4");
            newPhoto.setCaption(txt);
            sendPhoto.setMedia(newPhoto);

            if(botik.isCart()){
                List<List<InlineKeyboardButton>> buttons = List.of(
                     //  List.of(newButton("📄 PAYMENT ASK", "payment:" +"payment_ask" + ":" + botId)),


                                List.of(newButton("✏\uFE0F Текст", "TEXT:" +"send_money" + ":" + botId)),
                                List.of(newButton("\uD83D\uDCF8 Фото", "PHOTO:" +"send_money"  + ":" + botId)),
                                List.of(newButton("\uD83C\uDFAC Відео", "VIDEO:" +"send_money"  + ":" + botId)),
                             //   List.of(newButton("Назад", "RENAME_MESSAGE:" +parts[0]  + ":" + botId)),

                      //  List.of(newButton("🖼 SENDMONEY_ASK", "payment:" +"send_money" + ":" + botId)),
                        List.of(newButton("❌ УДАЛИТЬ МЕТОД", "DELETE_METHOD:" +key  + ":" + botId)),
                List.of(newButton("🔙 Назад", "payment:" +"payment_methods"  + ":" + botId)));

                markup.setKeyboard(buttons);

            }
            else{

                List<List<InlineKeyboardButton>> buttons = List.of(
                        List.of(newButton("\uD83D\uDCB5 Добавить метод", "ADD_METHOD:" +key  + ":" + botId)),
                        List.of(newButton("🔙 Назад", "payment:" +"payment_methods"  + ":" + botId)));
                markup.setKeyboard(buttons);

            }
            sendPhoto.setReplyMarkup(markup);

        }


        try{
            bot.execute(sendPhoto);
        }
        catch(TelegramApiException e){
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
