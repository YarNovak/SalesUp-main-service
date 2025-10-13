package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.MediaForProd_deciding;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony_katalog;
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
import java.util.List;
import java.util.Optional;

@Component
public class SetProd_Media implements CallbackHandler {

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return (callbackData.startsWith("SET_PROD_media_"));
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String[] parts = query.getData().split(":");

        Long vapecomponyKatalog_id = Long.parseLong(parts[1]);
        Long botId = Long.parseLong(parts[2]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        Vapecompony_katalog vapecompony_katalog = vapecomponyKatalogRepository.findByIdAndBot_Id(vapecomponyKatalog_id, botId).orElse(null);

        if(parts[0].equals("SET_PROD_media_DEFAULT")){

            vapecompony_katalog .setPhoto(null);
            vapecompony_katalog .setPhotoMimeType(null);

            vapecompony_katalog .setVideo(null);
            vapecompony_katalog .setVideoMimeType(null);

            vapecomponyKatalogRepository.save(vapecompony_katalog);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(query.getMessage().getChatId().toString());
            sendMessage.setText("\uD83D\uDDD1 Медиа теперь отсутствуют");

            try {
               // bot.execute(sendMessage);

                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }
        else if(parts[0].equals("SET_PROD_media_PHOTO")){



            EditMessageMedia sendPhoto = new EditMessageMedia();
            sendPhoto.setMessageId(query.getMessage().getMessageId());
            sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));

            InputMediaPhoto inputMediaPhoto = new InputMediaPhoto("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/productDSPTV.png");
            inputMediaPhoto.setParseMode("MarkDownV2");

            if(vapecompony_katalog.getPhoto() != null){

               // ByteArrayInputStream bais = new ByteArrayInputStream(vapecompony_katalog.getPhoto());
               // InputFile inputFile = new InputFile(bais, "image.jpg");
               // sendPhoto.setPhoto(inputFile);

               inputMediaPhoto.setCaption(escapeMarkdown("\uD83D\uDCF8") +"*_Редактирование фото_*\n" +
                        "\n" +
                        escapeMarkdown("⚙️\uFE0F")+" модифицируйте в любой момент");



                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> buttons = List.of(
                        List.of(newButton("DELETE", "PRODUCTMEDIA_DELETE:" +vapecomponyKatalog_id  + ":" + botId)),
                        List.of(newButton("CHANGE", "PRODUCTMEDIA_CHANGE:" +vapecomponyKatalog_id  + ":" + botId)),
                        List.of(newButton("НАЗАД", "SET_FORPRODMEDIA_YES:" +vapecomponyKatalog_id  + ":" + botId))

                );
                markup.setKeyboard(buttons);
                sendPhoto.setReplyMarkup(markup);

            }

            else {
                inputMediaPhoto.setParseMode("MarkDownV2");
                //sendPhoto.setPhoto(new InputFile("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png"));
                inputMediaPhoto.setCaption(escapeMarkdown("\uD83C\uDFAC") +"*_Редактирование видео_*\n" +
                        "\n" +
                        escapeMarkdown("⚙️\uFE0F")+" модифицируйте в любой момент");

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> buttons = List.of(
                        List.of(newButton("ADD", "PRODUCTMEDIA_ADD:" +vapecomponyKatalog_id  + ":" + botId)),
                        List.of(newButton("НАЗАД", "SET_FORPRODMEDIA_YES:" +vapecomponyKatalog_id  + ":" + botId))
                );
                markup.setKeyboard(buttons);
                sendPhoto.setReplyMarkup(markup);

            }

            try{
                sendPhoto.setMedia(inputMediaPhoto);
                bot.execute(sendPhoto);

              //  Message msgg =  bot.execute(sendPhoto);
              //  messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
            }
            catch (TelegramApiException e){
                e.printStackTrace();
            }
        }

        else if(parts[0].equals("SET_PROD_media_VIDEO")){

            Optional<Vapecompony_katalog> v = vapecomponyKatalogRepository.findByIdAndBot_Id(vapecomponyKatalog_id, botId);

            if(v.get().getVideo() != null){

                EditMessageMedia sendVideo = new  EditMessageMedia();
                sendVideo.setMessageId(query.getMessage().getMessageId());
                sendVideo.setChatId(String.valueOf(query.getMessage().getChatId()));

                Vapecompony_katalog vape = vapecomponyKatalogRepository.findByIdAndBot_Id(vapecomponyKatalog_id, botId).orElseThrow(() -> new RuntimeException("Медіа не знайдено"));
               //// ByteArrayInputStream bais = new ByteArrayInputStream(vape.getVideo());
             //   InputFile inputFile = new InputFile(bais, "video.mp4");
               // sendVideo.setVideo(inputFile);
              //  sendVideo.setCaption("You have this one");

                InputMediaPhoto inputMediaPhoto = new InputMediaPhoto("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png");
                inputMediaPhoto.setParseMode("MarkDownV2");
                inputMediaPhoto.setCaption(escapeMarkdown("\uD83C\uDFAC") +"*_Редактирование видео_*\n" +
                        "\n" +
                        escapeMarkdown("⚙️\uFE0F")+" модифицируйте в любой момент");

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> buttons = List.of(
                        List.of(newButton("DELETE", "PRODUCTMEDIAVIDEO_DELETE:" +vapecomponyKatalog_id  + ":" + botId)),
                        List.of(newButton("CHANGE", "PRODUCTMEDIAVIDEO_CHANGE:" +vapecomponyKatalog_id  + ":" + botId)),
                        List.of(newButton("НАЗАД", "SET_FORPRODMEDIA_YES:" +vapecomponyKatalog_id  + ":" + botId))
                );
                markup.setKeyboard(buttons);
                sendVideo.setReplyMarkup(markup);

                try{
                    sendVideo.setMedia(inputMediaPhoto);
                    bot.execute(sendVideo);

                  //  Message msgg =  bot.execute(sendVideo);
                 //   messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                }
                catch (TelegramApiException e){
                    e.printStackTrace();
                }
            }

            else{

                EditMessageMedia sendPhoto = new EditMessageMedia();

                sendPhoto.setMessageId(query.getMessage().getMessageId());

                sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));
                InputMediaPhoto inputMediaPhoto = new InputMediaPhoto("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png");
                inputMediaPhoto.setParseMode("MarkDownV2");
                inputMediaPhoto.setCaption(escapeMarkdown("\uD83C\uDFAC") +"*_Редактирование видео_*\n" +
                        "\n" +
                        escapeMarkdown("⚙️\uFE0F")+" модифицируйте в любой момент");

                //sendPhoto.setPhoto(new InputFile("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png"));
                //sendPhoto.setCaption("You have nea");

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> buttons = List.of(
                        List.of(newButton("ADD", "PRODUCTMEDIAVIDEO_ADD:" +vapecomponyKatalog_id  + ":" + botId)),
                        List.of(newButton("НАЗАД", "SET_FORPRODMEDIA_YES:" +vapecomponyKatalog_id  + ":" + botId))
                );
                markup.setKeyboard(buttons);
                sendPhoto.setReplyMarkup(markup);

                sendPhoto.setMedia(inputMediaPhoto);
                try{
                  bot.execute(sendPhoto);

                    //Message msgg =  bot.execute(sendPhoto);
                    //messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                }
                catch (TelegramApiException e){
                    e.printStackTrace();
                }

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
