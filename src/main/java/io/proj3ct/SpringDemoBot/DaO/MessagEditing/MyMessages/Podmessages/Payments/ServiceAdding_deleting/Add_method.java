package io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.Podmessages.Payments.ServiceAdding_deleting;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;


@Component
public class Add_method implements CallbackHandler {

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("ADD_METHOD:");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String[] parts = query.getData().split(":");

        String key = parts[1];      // catalog, cart, ...
        Long botId = Long.parseLong(parts[2]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);
        EditMessageCaption editCaption = new EditMessageCaption();
        editCaption.setChatId(String.valueOf(query.getMessage().getChatId()));
        editCaption.setMessageId(query.getMessage().getMessageId());

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();


        if(key.equals("Nalichka")){
            editCaption.setParseMode("MarkDownV2");
            editCaption.setCaption((escapeMarkdown("\uD83D\uDCB0") +"*_Наличные_*\n" +
                    "\n" +
                    "> "+escapeMarkdown("\uD83D\uDCB8")+"Оплата при получении заказа — удобно и просто"+escapeMarkdown(".")+ "Подходит для самовывоза и доставки"+escapeMarkdown("\uD83D\uDC4C") ));
            List<List<InlineKeyboardButton>> buttons = List.of(
                    List.of(newButton("❌ УДАЛИТЬ МЕТОД", "DELETE_METHOD:" +key  + ":" + botId)),
                    List.of(newButton("\uD83D\uDD19 Назад", "payment:" +"payment_methods"  + ":" + botId)));
            markup.setKeyboard(buttons);

            Bot botik = botRepository.findById(botId).get();
            if(key.equals("Nalichka")){

                botik.setNalichka(true);

            }
            else{
                botik.setCart(true);
            }
            botRepository.save(botik);

            editCaption.setReplyMarkup(markup);

            try {
                bot.execute(editCaption);
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }
        else{
            editCaption.setParseMode("MarkDownV2");
            editCaption.setCaption(escapeMarkdown("\uD83C\uDF10")+ "_*Онлайн оплата*_\n" +
                    "\n" +
                    "> Карты, переводы, криптовалюту и другие доступные методы — удобно и универсально для всех клиентов"+escapeMarkdown("\uD83D\uDC4C") +"\n"+
                    "\n" +
                    escapeMarkdown("\uD83D\uDCA1") +"Измените текст и медиа");


            List<List<InlineKeyboardButton>> buttons = List.of(
                    List.of(newButton("✏\uFE0F Текст", "TEXT:" +key  + ":" + botId)),
                    List.of(newButton("\uD83D\uDCF8 Фото", "PHOTO:" +key  + ":" + botId)),
                    List.of(newButton("\uD83C\uDFAC Відео", "VIDEO:" +key  + ":" + botId)),
                    List.of(newButton("❌ УДАЛИТЬ МЕТОД", "DELETE_METHOD:" +key  + ":" + botId)),
                    List.of(newButton("\uD83D\uDD19 Назад", "payment:" +"payment_methods"  + ":" + botId)));
            markup.setKeyboard(buttons);

            Bot botik = botRepository.findById(botId).get();
            if(key.equals("Nalichka")){

                botik.setNalichka(true);

            }
            else{
                botik.setCart(true);
            }
            botRepository.save(botik);

            editCaption.setReplyMarkup(markup);

            try {
                bot.execute(editCaption);
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }
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
