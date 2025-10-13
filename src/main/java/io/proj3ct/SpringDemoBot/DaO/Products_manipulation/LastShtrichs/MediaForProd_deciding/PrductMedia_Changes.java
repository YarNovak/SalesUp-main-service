package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.MediaForProd_deciding;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddProductPhoto_hadnler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddProductVideo_handler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony_katalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;

@Component
public class PrductMedia_Changes implements CallbackHandler  {

    @Autowired
    private AddProductPhoto_hadnler addProductPhotoHadnler;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private AddProductVideo_handler addProductVideoHandler;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {

        return (callbackData.startsWith("PRODUCTMEDIAVIDEO_") || callbackData.startsWith("PRODUCTMEDIA_"));

    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String[] parts = query.getData().split(":");
        if (parts.length != 3) {return;}

        Long vapecomponyKatalog_id = Long.parseLong(parts[1]);
        Long botId = Long.parseLong(parts[2]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        if(parts[0].equals("PRODUCTMEDIA_ADD")){
/*
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));

            sendPhoto.setPhoto(new InputFile("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png"));
            sendPhoto.setCaption("SEND MEEEEEE");
*/
            SendMessage sendPhoto = new SendMessage();
            sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendPhoto.setText("\uD83D\uDCF7 Отправьте новое фото:");

            addProductPhotoHadnler.add(query.getFrom().getId(), botId, vapecomponyKatalog_id);


            try{
            //    bot.execute(sendPhoto);

                Message msgg =  bot.execute(sendPhoto);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }
        }

        else if(parts[0].equals("PRODUCTMEDIA_DELETE")){

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendMessage.setText("🗑 Фото успешно удалено");

            Vapecompony_katalog vapecomponyKatalog = vapecomponyKatalogRepository.findByIdAndBot_Id(vapecomponyKatalog_id, botId).get();
            vapecomponyKatalog.setPhoto(null);
            vapecomponyKatalog.setPhotoMimeType(null);

            vapecomponyKatalogRepository.save(vapecomponyKatalog);
            try {
               // bot.execute(sendMessage);

                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        else if(parts[0].equals("PRODUCTMEDIA_CHANGE")){
/*
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));

            Vapecompony_katalog vapecomponyKatalog = vapecomponyKatalogRepository.findByIdAndBot_Id(vapecomponyKatalog_id, botId).get();
            ByteArrayInputStream bais = new ByteArrayInputStream(vapecomponyKatalog.getPhoto());
            InputFile inputFile = new InputFile(bais, "image.jpg");
            sendPhoto.setPhoto(inputFile);
            sendPhoto.setCaption("\uD83D\uDCF7 Отправьте новое фото:");

 */

            SendMessage sendPhoto = new SendMessage();
            sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendPhoto.setText("\uD83D\uDCF7 Отправьте новое фото:");

            addProductPhotoHadnler.expectPhotoRename(query.getFrom().getId(), botId, vapecomponyKatalog_id);
            try{
               // bot.execute(sendPhoto);
                Message msgg =  bot.execute(sendPhoto);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch(TelegramApiException e) {
                e.printStackTrace();
            }
        }

        else if(parts[0].equals("PRODUCTMEDIAVIDEO_ADD")){
/*
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));

            sendPhoto.setPhoto(new InputFile("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png"));
            sendPhoto.setCaption("SEND MEEEEEE");

 */
            SendMessage sendPhoto = new SendMessage();
            sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendPhoto.setText("\uD83C\uDFAC Отправьте новое видео:");

            addProductVideoHandler.add(query.getFrom().getId(), botId, vapecomponyKatalog_id);


            try{
               // bot.execute(sendPhoto);

                Message msgg =  bot.execute(sendPhoto);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }
        }
        else if(parts[0].equals("PRODUCTMEDIAVIDEO_DELETE")){

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendMessage.setText("\uD83D\uDDD1 Видео успешно удалено");

            Vapecompony_katalog vapecompony_katalog = vapecomponyKatalogRepository.findByIdAndBot_Id(vapecomponyKatalog_id, botId).get();
            vapecompony_katalog.setVideo(null);
            vapecompony_katalog.setVideoMimeType(null);

            vapecomponyKatalogRepository.save(vapecompony_katalog);

            try {
             //   bot.execute(sendMessage);

                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }

        else if(parts[0].equals("PRODUCTMEDIAVIDEO_CHANGE")){
/*
            SendVideo sendVideo = new SendVideo();
            sendVideo.setChatId(String.valueOf(query.getMessage().getChatId()));
            Vapecompony_katalog vapecompony_katalog = vapecomponyKatalogRepository.findByIdAndBot_Id(vapecomponyKatalog_id, botId).get();

            ByteArrayInputStream bais = new ByteArrayInputStream(vapecompony_katalog.getVideo());
            InputFile inputFile = new InputFile(bais, "video.mp4");
            sendVideo.setVideo(inputFile);
            sendVideo.setCaption("\uD83C\uDFAC Отправьте новое видео:");

 */

            SendMessage sendVideo = new SendMessage();
            sendVideo.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendVideo.setText("\uD83C\uDFAC Отправьте новое видео:");

            addProductVideoHandler.add(query.getFrom().getId(), botId, vapecomponyKatalog_id);

            try{
               // bot.execute(sendVideo);

                Message msgg =  bot.execute(sendVideo);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch(TelegramApiException e) {
                e.printStackTrace();
            }

        }


    }
}
