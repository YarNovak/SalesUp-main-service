package io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.VideoEditing.VideoAddDeleteChange;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.BotMessage;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.VideoEditing.Video_handler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.LinkDecider;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.repository.BotMessageRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class VideoChangers implements CallbackHandler{

    @Autowired
    private BotMessageRepository botMessageRepository;
    @Autowired
    private BotRepository botRepository;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("VIDEO_");
    }

    @Autowired
    private Video_handler video_handler;


    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        String[] parts = query.getData().split(":");
        if (parts.length != 3) {return;}

        String key = parts[1];      // catalog, cart, ...
        Long botId = Long.parseLong(parts[2]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        if(parts[0].equals("VIDEO_ADD")){

            System.out.println("8888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888888");
           /*
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));

            sendPhoto.setPhoto(new InputFile("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png"));
            sendPhoto.setCaption("SEND MEEEEEE");

            */

            SendMessage sendPhoto = new SendMessage();
            sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendPhoto.setText("\uD83C\uDFAC Отправьте новое видео:");

            video_handler.add(query.getFrom().getId(), botId, key);

            try{
               // bot.execute(sendPhoto);

                Message msgg =  bot.execute(sendPhoto);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }

        }
        else if(parts[0].equals("VIDEO_DELETE")){

            EditMessageMedia editMessageMedia = new EditMessageMedia();
            editMessageMedia.setMessageId(query.getMessage().getMessageId());

            editMessageMedia.setChatId(String.valueOf(query.getMessage().getChatId()));

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            markup.setKeyboard(create_buttons(key, botId, 0L));
            editMessageMedia.setReplyMarkup(markup);

            InputMediaPhoto newPhoto = new InputMediaPhoto();
            newPhoto.setParseMode("MarkDownV2");
            newPhoto.setCaption(escapeMarkdown("\uD83D\uDCF8") +"*_Редактирование видео_*\n" +
                    "\n" +
                    escapeMarkdown("⚙️\uFE0F")+" модифицируйте в любой момент");

            newPhoto.setMedia(LinkDecider.getPhotoMessageLink(key)); //!/
            //    sendMessage.setMedia(newPhoto);

            editMessageMedia.setMedia(newPhoto);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));
          //  sendMessage.setMessageId(query.getMessage().getMessageId());

          //  InputMediaPhoto newPhoto = new InputMediaPhoto();
          //  newPhoto.setMedia("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png");
          //  newPhoto.setCaption("I've deleted that beatch");

           // sendMessage.setMedia(newPhoto);
            sendMessage.setText("\uD83D\uDDD1 Видео успешно удалено");

            BotMessage botMessage = botMessageRepository.findByMessageKeyAndBot_Id(key, botId).get();
            botMessage.setVideo(null);
            botMessage.setVideoMimeType(null);
            botMessage.setVideo_updatedAt(null);

            Bot bot1 = botRepository.findById(botId).get();
            bot1.setActive(false);
            botRepository.save(bot1);

            botMessageRepository.save(botMessage);

            try {
                bot.execute(sendMessage);
                bot.execute(editMessageMedia);
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }
        else if(parts[0].equals("VIDEO_CHANGE")){
/*
            SendVideo sendVideo = new SendVideo();
            sendVideo.setChatId(String.valueOf(query.getMessage().getChatId()));

            BotMessage bm = botMessageRepository.findByMessageKeyAndBot_Id(key, botId) .orElseThrow(() -> new RuntimeException("Медіа не знайдено"));
            ByteArrayInputStream bais = new ByteArrayInputStream(bm.getVideo());
            InputFile inputFile = new InputFile(bais, "video.mp4");
            sendVideo.setVideo(inputFile);
            sendVideo.setCaption("You have this one, change, mother fucker)");
*/

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendMessage.setText("\uD83C\uDFAC Отправьте новое видео:");

            video_handler.expectPhotoRename(query.getFrom().getId(), botId, key);

            try{
               // bot.execute(sendVideo);

                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch(TelegramApiException e) {
                e.printStackTrace();
            }

        }

    }

    private InlineKeyboardButton newButton(String text, String callbackData) {
        InlineKeyboardButton btn = new InlineKeyboardButton(text);
        btn.setCallbackData(callbackData);
        return btn;
    }

    /*
    List<PhotoSize> photos = sentMessage.getPhoto();
if (photos != null && !photos.isEmpty()) {
        String fileId = photos.get(photos.size() - 1).getFileId(); // найбільший розмір
        System.out.println("file_id = " + fileId);
    }

 */

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
    private List<List<InlineKeyboardButton>> create_buttons(String key, Long botId, Long type){

        List<List<InlineKeyboardButton>> buttons ;
        if(type == 0){
            buttons =  new java.util.ArrayList<>( List.of(
                    List.of(newButton("\uD83C\uDFACДобавить", "VIDEO_ADD:" +key  + ":" + botId))
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

}
