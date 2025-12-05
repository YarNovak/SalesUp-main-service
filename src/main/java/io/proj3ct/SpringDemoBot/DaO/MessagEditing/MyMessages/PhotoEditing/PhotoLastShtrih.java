package io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.PhotoEditing;


import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.BotMessage;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.Config.BotConfig;
import io.proj3ct.SpringDemoBot.model.UserRepository;
import io.proj3ct.SpringDemoBot.repository.BotMessageRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Component
public class PhotoLastShtrih implements MessageHandle {


    @Autowired
    private BotRepository botRepository;
    @Autowired
    private Photo_handler photo_handler;
    @Autowired
    private BotMessageRepository botMessageRepository;
    @Autowired
    private BotConfig botConfig;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(org.telegram.telegrambots.meta.api.objects.Message msg) {
        return (   (photo_handler.getRenameRequest(msg.getFrom().getId()) != null)  || (photo_handler.getaddRequest(msg.getFrom().getId()) != null ));
    }

    @Override
    public void handle(org.telegram.telegrambots.meta.api.objects.Message msg, TelegramLongPollingBot bot) {
        Long userId = msg.getFrom().getId();
        Photo_handler.RenameRequest request = photo_handler.getRenameRequest(userId);

        if (request == null) {

           request = photo_handler.getaddRequest(userId);


        }

        Optional<Bot> botOpt = botRepository.findById(request.botId);
        if (botOpt.isEmpty()) return;

        Bot clientBot = botOpt.get();


        List<PhotoSize> photos = msg.getPhoto();
        PhotoSize bestPhoto = photos.get(photos.size() - 1);
        String fileId = bestPhoto.getFileId();

        // Отримати file_path
        GetFile getFile = new GetFile(fileId);
        File file = null;
        try {
            file = bot.execute(getFile);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        String filePath = file.getFilePath();

        System.out.println(filePath);
        System.out.println("mama");

        // Скачати фото
        String downloadUrl = "https://api.telegram.org/file/bot" + botConfig.getToken() + "/" + filePath;
        byte[] photoBytes;

        try (InputStream in = new URL(downloadUrl).openStream()) {
            photoBytes = in.readAllBytes();

        }


        catch (IOException e) {


            throw new RuntimeException(e);


        }




        if(photo_handler.getRenameRequest(userId) != null){
            BotMessage botMessage = botMessageRepository.findByMessageKeyAndBot_Id(photo_handler.getRenameRequest(userId).key, clientBot.getId()).get();





           // botMessage.setPhoto_updatedAt(LocalDateTime.now());
            botMessage.setPhoto(photoBytes);
            botMessage.setPhotoMimeType("image/jpeg");


          //  photo_handler.clear_add(userId);
            botMessageRepository.save(botMessage);

            photo_handler.clear(userId);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(msg.getChatId().toString());
            sendMessage.setText("✅ Фото успешно обновлено");

            clientBot.setActive(false);
            botRepository.save(clientBot);

            try {
               // bot.execute(sendMessage);

                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }
        else if(photo_handler.getaddRequest(userId) != null){
            System.out.println("000000000000000000000000011111111111111111111111111111111110000000000000000");
            BotMessage botMessage = botMessageRepository.findByMessageKeyAndBot_Id(photo_handler.getaddRequest(userId).key, clientBot.getId()).get();


            botMessage.setPhoto(photoBytes);
            botMessage.setPhotoMimeType("image/jpeg");



           // botik.getBotmessages().add(botMessage);
/*
            List<BotMessage> bm = botik.getBotmessages();
            bm.add(botMessage);
            botik.setBotmessages(bm);
*/
          //  botRepository.save(botik);

            botMessageRepository.save(botMessage);
            clientBot.setActive(false);
            botRepository.save(clientBot);


        //    User user = new User();
           // user.setBot(botRepository.findById(photo_handler.getaddRequest(userId).getBotId()).get());
          //  user.setChatId(123L);

         //   userRepository.save(user);

            System.out.println("aaaaaaaaaaaaaaaaaaa");
          //  botRepository.save(botik);


            System.out.println(botMessage.getMessageKey() +" "+botMessage.getPhotoMimeType()+ " " + botMessage.getBot().getId());




          //  botMessageRepository.save(botMessage);
            photo_handler.clear_add(userId);



            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(msg.getChatId().toString());
            sendMessage.setText("✅ Фото успешно обновлено");

            try {
                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }


    }
}
