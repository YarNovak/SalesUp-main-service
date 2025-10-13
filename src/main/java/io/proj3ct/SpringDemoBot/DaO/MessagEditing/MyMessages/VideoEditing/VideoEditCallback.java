package io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.VideoEditing;

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
public class VideoEditCallback implements CallbackHandler{


    @Autowired
    private BotMessageRepository botMessageRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("VIDEO:");
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
        newPhoto.setMedia(LinkDecider.getPhotoMessageLink(key)); //!/



        Optional<BotMessage> b = botMessageRepository.findByMessageKeyAndBot_Id(key, botId);
        System.out.println("99999999999999999999999999999999999999999999999999999999999999");
        if(b.get().getVideo() != null) {

         //   SendVideo sendVideo = new SendVideo();
         //   sendVideo.setChatId(String.valueOf(query.getMessage().getChatId()));

            System.out.println("99999999999999999999999999999999999999999999999999999999999999");
            BotMessage bm = botMessageRepository.findByMessageKeyAndBot_Id(key, botId) .orElseThrow(() -> new RuntimeException("Медіа не знайдено"));
            ByteArrayInputStream bais = new ByteArrayInputStream(bm.getVideo());
            InputFile inputFile = new InputFile(bais, "video.mp4");
        //    sendVideo.setVideo(inputFile);
        //    sendVideo.setCaption("You have this one");


            newPhoto.setParseMode("MarkDownV2");
            newPhoto.setCaption(escapeMarkdown("\uD83C\uDFAC") +"*_Редактирование видео_*\n" +
                    "\n" +
                    escapeMarkdown("⚙️\uFE0F")+" модифицируйте в любой момент");

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> buttons = List.of(
                    List.of(newButton("❌УДАЛИТЬ", "VIDEO_DELETE:" +key  + ":" + botId)),
                    List.of(newButton("\uD83C\uDFAC ИЗМЕНИТЬ", "VIDEO_CHANGE:" +key  + ":" + botId))
            );
            markup.setKeyboard(create_buttons(key, botId, 1L));
        //    sendVideo.setReplyMarkup(markup);

            sendPhoto.setReplyMarkup(markup);
            sendPhoto.setMedia(newPhoto);

            try{
                bot.execute(sendPhoto);
            }
            catch (TelegramApiException e){
                e.printStackTrace();
            }

        }
        else{

           // SendPhoto sendPhoto = new SendPhoto();
           // sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));
           // sendPhoto.setPhoto(new InputFile("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png"));
            // sendPhoto.setCaption("You have nea");

            newPhoto.setParseMode("MarkDownV2");
            newPhoto.setCaption(escapeMarkdown("\uD83C\uDFAC") +"*_Редактирование видео_*\n" +
                    "\n" +
                    escapeMarkdown("⚙️\uFE0F")+" модифицируйте в любой момент");

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> buttons = List.of(
                    List.of(newButton("\uD83C\uDFAC Добавить", "VIDEO_ADD:" +key  + ":" + botId))
            );
            markup.setKeyboard(create_buttons(key, botId, 0L));
            sendPhoto.setReplyMarkup(markup);
            sendPhoto.setMedia(newPhoto);
            try{
                bot.execute(sendPhoto);
            }
            catch (TelegramApiException e){
                e.printStackTrace();
            }

        }


    }

    private InlineKeyboardButton newButton(String text, String callbackData) {
        InlineKeyboardButton btn = new InlineKeyboardButton(text);
        btn.setCallbackData(callbackData);
        return btn;
    }

    private List<List<InlineKeyboardButton>> create_buttons(String key, Long botId, Long type){

        List<List<InlineKeyboardButton>> buttons ;
        if(type == 0){
            buttons =  new java.util.ArrayList<>( List.of(
                    List.of(newButton("\uD83C\uDFAC Добавить", "VIDEO_ADD:" +key  + ":" + botId))
            ));

        }
        else{
                      buttons  = new java.util.ArrayList<>( List.of(
                    List.of(newButton("❌УДАЛИТЬ", "VIDEO_DELETE:" +key  + ":" + botId)),
                    List.of(newButton("\uD83C\uDFAC ИЗМЕНИТЬ", "VIDEO_CHANGE:" +key  + ":" + botId))
            ));
        }


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
