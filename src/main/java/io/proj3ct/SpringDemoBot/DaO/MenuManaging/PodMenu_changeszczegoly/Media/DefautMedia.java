package io.proj3ct.SpringDemoBot.DaO.MenuManaging.PodMenu_changeszczegoly.Media;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;

import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
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
import java.util.Optional;

@Component
public class DefautMedia implements CallbackHandler {

    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
       return (callbackData.startsWith("SET_VAPEMEDIA_"));
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {




        String[] parts = query.getData().split(":");

        Long vapecompony_id = Long.parseLong(parts[1]);
        Long botId = Long.parseLong(parts[2]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        Vapecompony vapecompony = vapecomponyRepository.findByIdAndBot_Id(vapecompony_id, botId).get();
        if(parts[0].equals("SET_VAPEMEDIA_DEFAULT")){

            vapecompony.setPhoto(null);
            vapecompony.setPhotoMimeType(null);

            vapecompony.setVideo(null);
            vapecompony.setVideoMimeType(null);

            vapecomponyRepository.save(vapecompony);

            EditMessageMedia sendMessage = new EditMessageMedia();
            sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendMessage.setMessageId(query.getMessage().getMessageId());

            InputMediaPhoto newPhoto = new InputMediaPhoto();
            newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/menuDST.png");
            newPhoto.setCaption("Підменюшка без media");

            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

            keyboard.add(List.of(button("\uD83D\uDD04 ПО УМОЛЧАНИЮ", "SET_VAPEMEDIA_DEFAULT:" + vapecompony_id + ":" + botId)));
            keyboard.add(List.of(button("\uD83D\uDCF8 Фото", "SET_VAPEMEDIA_PHOTO:"+ vapecompony_id + ":" + botId)));
            keyboard.add(List.of(button("\uD83C\uDFAC Видео", "SET_VAPEMEDIA_VIDEO:"+ vapecompony_id + ":" + botId)));
            keyboard.add(List.of(button("\uD83D\uDD19 Назад", "EDIT_PODMENU:" + vapecompony_id+":" + botId)));

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);
            sendMessage.setReplyMarkup(markup);

            sendMessage.setMedia(newPhoto);

            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(query.getMessage().getChatId()));
            message.setText("\uD83D\uDDD1 Медиа теперь отсутствуют");

            try {
                Message m = bot.execute(message);
                messageRegistry.addMessage(m.getChatId(), m.getMessageId());
            //    bot.execute(sendMessage);

            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if(parts[0].equals("SET_VAPEMEDIA_PHOTO")){
            System.out.println("KIZAK");
            if(vapecompony.getPhoto() != null){

                EditMessageMedia sendPhoto = new EditMessageMedia();
                sendPhoto.setMessageId(query.getMessage().getMessageId());
                sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));

                ByteArrayInputStream bais = new ByteArrayInputStream(vapecompony.getPhoto());
                InputFile inputFile = new InputFile(bais, "image.jpg");

                InputMediaPhoto newPhoto = new InputMediaPhoto();
                newPhoto.setMedia("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png");
                newPhoto.setCaption("Підменюшка без media");

                sendPhoto.setMedia(newPhoto);

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> buttons = List.of(
                        List.of(newButton("DELETE", "VAPEMEDIA_DELETE:" +vapecompony_id  + ":" + botId)),
                        List.of(newButton("CHANGE", "VAPEMEDIA_CHANGE:" +vapecompony_id  + ":" + botId)),
                        List.of(newButton("НАЗАД", "CHANGE_PODMENU_MEDIA:" +vapecompony_id  + ":" + botId))
                );
                markup.setKeyboard(buttons);
                sendPhoto.setReplyMarkup(markup);
                try{
                    bot.execute(sendPhoto);
                }
                catch (TelegramApiException e){
                    e.printStackTrace();
                }

            }
            else {

                EditMessageMedia sendMessage = new EditMessageMedia();
                sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));
                sendMessage.setMessageId(query.getMessage().getMessageId());

                InputMediaPhoto newPhoto = new InputMediaPhoto();
                newPhoto.setMedia("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png");
                newPhoto.setCaption("You have new");
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> buttons = List.of(
                        List.of(newButton("ADD", "VAPEMEDIA_ADD:" +vapecompony_id  + ":" + botId)),
                        List.of(newButton("НАЗАД", "CHANGE_PODMENU_MEDIA:" +vapecompony_id  + ":" + botId))

                );
                markup.setKeyboard(buttons);
                sendMessage.setReplyMarkup(markup);

                sendMessage.setMedia(newPhoto);

                try{
                    bot.execute(sendMessage);
                }
                catch (TelegramApiException e){
                    e.printStackTrace();
                }

            }




        }

        else if(parts[0].equals("SET_VAPEMEDIA_VIDEO")){


            Optional<Vapecompony> v = vapecomponyRepository.findByIdAndBot_Id(vapecompony_id, botId);

            if(v.get().getVideo() != null){

                EditMessageMedia sendVideo = new EditMessageMedia();
                sendVideo.setChatId(String.valueOf(query.getMessage().getChatId()));
                sendVideo.setMessageId(query.getMessage().getMessageId());

                Vapecompony vape = vapecomponyRepository.findByIdAndBot_Id(vapecompony_id, botId).orElseThrow(() -> new RuntimeException("Медіа не знайдено"));
                ByteArrayInputStream bais = new ByteArrayInputStream(vape.getVideo());
                InputFile inputFile = new InputFile(bais, "video.mp4");

                //sendVideo.setVideo(inputFile);
               // sendVideo.setCaption("You have this one");

                InputMediaPhoto newVideo = new InputMediaPhoto();
                newVideo.setMedia("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQgsY1SiTJotsRgDNtJ5XhAo_jLrZP2zPHc5w&s");
                newVideo.setParseMode("MarkDownV2");
                newVideo.setCaption(escapeMarkdown("\uD83C\uDFAC") +"*_Редактирование видео_*\n" +
                        "\n" +
                        escapeMarkdown("\uD83C\uDFAC")+" модифицируйте в любой момент");

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> buttons = List.of(
                        List.of(newButton("DELETE", "VIDEOVAPEMEDIA_DELETE:" +vapecompony_id  + ":" + botId)),
                        List.of(newButton("CHANGE", "VIDEOVAPEMEDIA_CHANGE:" +vapecompony_id  + ":" + botId)),
                        List.of(newButton("НАЗАД", "CHANGE_PODMENU_MEDIA:" +vapecompony_id  + ":" + botId))
                );
                markup.setKeyboard(buttons);
                sendVideo.setReplyMarkup(markup);

                try{
                  bot.execute(sendVideo);

                }
                catch (TelegramApiException e){
                    e.printStackTrace();
                }
            }
            else{

                EditMessageMedia sendMessage = new EditMessageMedia();
                sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));
                sendMessage.setMessageId(query.getMessage().getMessageId());

                InputMediaPhoto newPhoto = new InputMediaPhoto();
                newPhoto.setParseMode("MarkDownV2");
                newPhoto.setMedia("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png");
                newPhoto.setCaption(escapeMarkdown("\uD83C\uDFAC") +"*_Редактирование видео_*\n" +
                        "\n" +
                        escapeMarkdown("⚙️\uFE0F")+" модифицируйте в любой момент");

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> buttons = List.of(
                        List.of(newButton("ADD", "VIDEOVAPEMEDIA_ADD:" +vapecompony_id  + ":" + botId)),
                        List.of(newButton("НАЗАД", "CHANGE_PODMENU_MEDIA:" +vapecompony_id  + ":" + botId))
                );
                markup.setKeyboard(buttons);
                sendMessage.setReplyMarkup(markup);

                sendMessage.setMedia(newPhoto);

                try{
                    bot.execute(sendMessage);
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
