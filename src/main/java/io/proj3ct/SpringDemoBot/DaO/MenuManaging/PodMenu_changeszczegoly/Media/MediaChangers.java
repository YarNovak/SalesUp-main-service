package io.proj3ct.SpringDemoBot.DaO.MenuManaging.PodMenu_changeszczegoly.Media;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Photos.MenuPhoto_handler;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Videos.MenuVideo_handler;

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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;

@Component
public class MediaChangers implements CallbackHandler {

    @Autowired
    private MenuPhoto_handler menuPhoto_handler;

    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private MenuVideo_handler menuVideoHandler;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return (callbackData.startsWith("VAPEMEDIA_") || callbackData.startsWith("VIDEOVAPEMEDIA_"));
    }



    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String[] parts = query.getData().split(":");
        if (parts.length != 3) {return;}

        Long vapecompony_id = Long.parseLong(parts[1]);
        Long botId = Long.parseLong(parts[2]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        if(parts[0].equals("VAPEMEDIA_ADD")){
/*
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));

            sendPhoto.setPhoto(new InputFile("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png"));
            sendPhoto.setCaption("SEND MEEEEEE");


 */

            SendMessage sendPhoto = new SendMessage();
            sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendPhoto.setText("\uD83D\uDCF7 Отправьте новое фото:");

            menuPhoto_handler.add(query.getFrom().getId(), botId, vapecompony_id);

            try{
                //bot.execute(sendPhoto);

                Message msgg =  bot.execute(sendPhoto);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }


        }
        else if(parts[0].equals("VAPEMEDIA_DELETE")){

            EditMessageMedia sendMessage = new EditMessageMedia();
            sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendMessage.setMessageId(query.getMessage().getMessageId());

            InputMediaPhoto newPhoto = new InputMediaPhoto();
            newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/menuDST.png");
            newPhoto.setCaption("\uD83D\uDDD1 Фото успешно удаленоо");

            sendMessage.setMedia(newPhoto);

            Vapecompony vapecompony = vapecomponyRepository.findByIdAndBot_Id(vapecompony_id, botId).get();
            vapecompony.setPhoto(null);
            vapecompony.setPhotoMimeType(null);

            vapecomponyRepository.save(vapecompony);
            try {
                bot.execute(sendMessage);


            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }


        }
        else if(parts[0].equals("VAPEMEDIA_CHANGE")){
/*
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));

            Vapecompony vapecompony = vapecomponyRepository.findByIdAndBot_Id(vapecompony_id, botId).get();
            ByteArrayInputStream bais = new ByteArrayInputStream(vapecompony.getPhoto());
            InputFile inputFile = new InputFile(bais, "image.jpg");
            sendPhoto.setPhoto(inputFile);

 */
            SendMessage sendPhoto = new SendMessage();
            sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendPhoto.setText("\uD83D\uDCF7 Отправьте новое фото:");


            menuPhoto_handler.expectPhotoRename(query.getFrom().getId(), botId, vapecompony_id);
            try{
                //bot.execute(sendPhoto);

                Message msgg =  bot.execute(sendPhoto);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch(TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if(parts[0].equals("VIDEOVAPEMEDIA_ADD")){
/*
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));

            sendPhoto.setPhoto(new InputFile("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png"));
            sendPhoto.setCaption("SEND MEEEEEE");

 */
            SendMessage sendPhoto = new SendMessage();
            sendPhoto.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendPhoto.setText("\uD83C\uDFAC Отправьте новое видео:");

            menuVideoHandler.add(query.getFrom().getId(),  botId, vapecompony_id);

            try{
            //   bot.execute(sendPhoto);

                Message msgg =  bot.execute(sendPhoto);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }

        }
        else if(parts[0].equals("VIDEOVAPEMEDIA_DELETE")){
           SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(query.getMessage().getChatId()));
            //    sendMessage.setMessageId(query.getMessage().getMessageId());

         //   InputMediaPhoto newPhoto = new InputMediaPhoto();
         //   newPhoto.setMedia("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png");
          //  newPhoto.setCaption("I've deleted that beatch");


            sendMessage.setText("\uD83D\uDDD1 Видео успешно удалено");
           // sendMessage.setMedia(newPhoto);

            Vapecompony v = vapecomponyRepository.findByIdAndBot_Id(vapecompony_id, botId).get();
            v.setVideo(null);
            v.setVideoMimeType(null);

            vapecomponyRepository.save(v);

            try {
                bot.execute(sendMessage);
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }
        else if(parts[0].equals("VIDEOVAPEMEDIA_CHANGE")){
/*
            SendVideo sendVideo = new SendVideo();
            sendVideo.setChatId(String.valueOf(query.getMessage().getChatId()));

            Vapecompony vp = vapecomponyRepository.findByIdAndBot_Id(vapecompony_id, botId) .orElseThrow( () -> new RuntimeException("Медіа не знайдено"));

            ByteArrayInputStream bais = new ByteArrayInputStream(vp.getVideo());
            InputFile inputFile = new InputFile(bais, "video.mp4");
            sendVideo.setVideo(inputFile);
            sendVideo.setCaption("\uD83C\uDFAC Отправьте новое видео:");

 */

            SendMessage sendVideo = new SendMessage();
            sendVideo.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendVideo.setText("\uD83C\uDFAC Отправьте новое видео:");

            menuVideoHandler.expectPhotoRename(query.getFrom().getId(), botId, vapecompony_id);

            try{
               //bot.execute(sendVideo);

                Message msgg =  bot.execute(sendVideo);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch(TelegramApiException e) {
                e.printStackTrace();
            }

        }


    }
}
