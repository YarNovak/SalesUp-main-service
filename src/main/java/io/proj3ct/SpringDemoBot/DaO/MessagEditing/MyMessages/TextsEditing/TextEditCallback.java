package io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.TextsEditing;

import io.proj3ct.SpringDemoBot.DB_entities.BotMessage;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.LinkDecider;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.repository.BotMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Optional;

@Component
public class TextEditCallback implements CallbackHandler {

    @Autowired
    private BotMessageRepository botMessageRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;


    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("TEXT:");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String[] parts = query.getData().split(":");
        if (parts.length != 3) {return;}

        String key = parts[1];      // catalog, cart, ...
        Long botId = Long.parseLong(parts[2]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        EditMessageMedia sendPhoto = new EditMessageMedia();

        sendPhoto.setMessageId(query.getMessage().getMessageId());

        sendPhoto.setChatId(query.getMessage().getChatId().toString());

        InputMediaPhoto newPhoto = new InputMediaPhoto();
        newPhoto.setMedia(LinkDecider.getTextMessageLink(key)); //!/


        System.out.println("99999999999999999999999999999999999999999999999999999999999999");




            System.out.println("99999999999999999999999999999999999999999999999999999999999999");
            BotMessage bm = botMessageRepository.findByMessageKeyAndBot_Id(key, botId) .orElseThrow(() -> new RuntimeException("Медіа не знайдено"));
            newPhoto.setParseMode("MarkDownV2");

            newPhoto.setCaption(escapeMarkdown("\uD83D\uDCAC") +"*_Редактирование текста_*\n" +
                    "\n" +
                    escapeMarkdown("⚙\uFE0F")+" модифицируйте в любой момент");
            /*
            String text = escapeMarkdown("\uD83D\uDCAC") +"*_Тексты сообщений_*\n" +
                    "\n" +
                    "> "+escapeMarkdown("✏\uFE0F")+" Настройте под свой стиль или добавьте акценты\n" +
                    "> "+"\n" +
                    "> "+escapeMarkdown("\uD83D\uDCC4")+"Можно оставить вариант по умолчанию\n" +
                    "\n" +
                    escapeMarkdown("⚙\uFE0F")+" Редактирование доступно в любой момент";
*/

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

       // if(key.equals("greeting") || key.equals("menu"))






            markup.setKeyboard(create_buttons(key, botId));
            sendPhoto.setReplyMarkup(markup);
            sendPhoto.setMedia(newPhoto);

            try{
                bot.execute(sendPhoto);
            }
            catch (TelegramApiException e){
                e.printStackTrace();
            }




    }

    private InlineKeyboardButton newButton(String text, String callbackData) {
        InlineKeyboardButton btn = new InlineKeyboardButton(text);
        btn.setCallbackData(callbackData);
        return btn;
    }

    private List<List<InlineKeyboardButton>> create_buttons(String key, Long botId){

        List<List<InlineKeyboardButton>> buttons = new java.util.ArrayList<>(List.of(
                List.of(newButton("\uD83D\uDD04 ПО УМОЛЧАНИЮ", "TEXT_DEFAULT:" + key + ":" + botId)),
                List.of(newButton("✏\uFE0F ИЗМЕНИТЬ", "TEXT_CHANGE:" + key + ":" + botId))
        ));

        if(key.equals("greeting") || key.equals("menu")) buttons.add( List.of(newButton("🔙 Назад", "greeting:" +key  + ":" + botId)));
        else if(key.equals("catalog") || key.equals("please_whait")) buttons.add( List.of(newButton("🔙 Назад", "RENAME_MESSAGE:" +key  + ":" + botId)));
        else if(key.equals("cart") || key.equals("clearing") || key.equals("changing")  ) buttons.add( List.of(newButton("🔙 Назад", "cart:" +key  + ":" + botId)));
            // else if(key.equals("Nalichka") || key.equals("Karta")  ) buttons.add( List.of(newButton("Назад", "ADD_METHOD:" +key  + ":" + botId)));
        else  if(key.equals("phone") || key.equals("delivery") || key.equals("phone_thanks")) buttons.add( List.of(newButton("🔙 Назад", "payment:" +key  + ":" + botId)));
        else  if(key.equals("accept") || key.equals("deny") || key.equals("congrat") ) buttons.add( List.of(newButton("🔙 Назад", "payment_acception:" +key  + ":" + botId)));
        else if(key.equals("Nalichka")) buttons.add( List.of(newButton("🔙 Назад", key  + ":" + botId)));
        else if(key.equals("payment_ask") || key.equals("send_money") )buttons.add( List.of(newButton("🔙 Назад", "payment:"+key  + ":" + botId)));

        return buttons;
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
