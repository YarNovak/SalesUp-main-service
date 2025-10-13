package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.MyProducts;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class AddOrCheck implements CallbackHandler {


    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return ((callbackData.startsWith("EDIT_PRODUCT:")) || ((callbackData.startsWith("EDITEDIT_PRODUCT:"))));
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {





        String[] parts = query.getData().split(":");
        Long botId = Long.parseLong(parts[1]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        if(query.getData().startsWith("EDIT_PRODUCT:")){

            SendMessage message = new SendMessage();
            message.setParseMode("MarkDownV2");

            message.setText( escapeMarkdown("\uD83D\uDECD")+"_*Имеющиеся позиции?*_"+"\n" +
                    "\n" + "> "+
                    "Добавить обновить удалить старые?"+"\n"+ "> "+
                    "Всё под вашим контролем"+escapeMarkdown("\uD83D\uDC4C")+"\n\n"+
                    escapeMarkdown("\uD83D\uDCA1")+" Легко управляйте всем прямо здесь"+escapeMarkdown("."));


            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            keyboard.add(List.of(button("\uD83D\uDD0D посмотреть / удалить ❌", "PRODUCTS_OBSERVE:" + botId)));
            keyboard.add(List.of(button("✚ Добавить", "PRODUCT_ADD:" + botId)));
            keyboard.add(List.of(button("🔙 Назад", "TOTALLY_END")));

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);

            message.setChatId(query.getMessage().getChatId().toString());
           // message.setText("Додати новий продукт чи змінити щось із того що в тебе є?");
            message.setReplyMarkup(markup);

            try{

               // bot.execute(message);

                Message msgg =  bot.execute(message);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch (TelegramApiException e){
                e.printStackTrace();
            }
        }
        else{

            EditMessageText message = new EditMessageText();
            message.setParseMode("MarkDownV2");
            message.setMessageId(query.getMessage().getMessageId());

            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            keyboard.add(List.of(button("\uD83D\uDD0D посмотреть / удалить ❌", "PRODUCTS_OBSERVE:" + botId)));
            keyboard.add(List.of(button("✚ Добавить", "PRODUCT_ADD:" + botId)));
            keyboard.add(List.of(button("🔙 Назад", "TOTALLY_END")));

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);

            message.setChatId(query.getMessage().getChatId().toString());
            message.setText( escapeMarkdown("\uD83D\uDECD")+"_*Имеющиеся позиции?*_"+"\n" +
                    "\n" + "> "+
                    "Добавить обновить удалить старые?"+"\n"+ "> "+
                    "Всё под вашим контролем"+escapeMarkdown("\uD83D\uDC4C")+"\n\n"+
                    escapeMarkdown("\uD83D\uDCA1")+" Легко управляйте всем прямо здесь"+escapeMarkdown("."));
            message.setReplyMarkup(markup);

            try{

                bot.execute(message);

            }
            catch (TelegramApiException e){
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
