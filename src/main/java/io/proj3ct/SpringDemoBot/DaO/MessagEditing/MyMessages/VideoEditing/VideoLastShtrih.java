package io.proj3ct.SpringDemoBot.DaO.MessagEditing.MyMessages.VideoEditing;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.BotMessage;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.config.BotConfig;
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
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

@Component
public class VideoLastShtrih implements MessageHandle {

    @Autowired
    private BotRepository botRepository;
    @Autowired
    private Video_handler video_handler;
    @Autowired
    private BotMessageRepository botMessageRepository;
    @Autowired
    private BotConfig botConfig;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean support(org.telegram.telegrambots.meta.api.objects.Message msg) {
        return (   (video_handler.getRenameRequest(msg.getFrom().getId()) != null)  || (video_handler.getaddRequest(msg.getFrom().getId()) != null ));
    }

    @Override
    public void handle(org.telegram.telegrambots.meta.api.objects.Message msg, TelegramLongPollingBot bot) {


        System.out.println("VIDEO: " + msg.getVideo());
        System.out.println("FILE_ID: " + msg.getVideo().getFileId());
        System.out.println("BOT TOKEN: " + botConfig.getToken());



        Long userId = msg.getFrom().getId();
        Video_handler.RenameRequest request = video_handler.getRenameRequest(userId);

        if (request == null) {

            request = video_handler.getaddRequest(userId);


        }



        Optional<Bot> botOpt = botRepository.findById(request.botId);
        if (botOpt.isEmpty()) return;


        Bot clientBot = botOpt.get();


        Video video = msg.getVideo();
        String fileId = video.getFileId();


        Integer fileSize = video.getFileSize();
        if (fileSize != null && fileSize > 10 * 1024 * 1024) { // 10 МБ
            SendMessage message = new SendMessage();
            message.setChatId(msg.getChatId().toString());
            message.setText("⚠️ Видео слишком большое. Пожалуйста, отправьте видео размером до 10 МБ.");
            try {

               Message msgsg = bot.execute(message);
               messageRegistry.addMessage(msgsg.getChatId(), msgsg.getMessageId());

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return; // Не продовжуємо обробку цього великого відео
        }


        // Отримати file_path
        System.out.println("SOS");
        GetFile getFile = new GetFile(fileId);
        System.out.println("SOS");
        File file = null;
        // doesnt work
        try {
            file = bot.execute(getFile);
            System.out.println("SOSik");

        } catch (TelegramApiException e) {
            System.out.println("SOSi");
            throw new RuntimeException(e);
        }

        String filePath = file.getFilePath();

        // Скачати відео

        String downloadUrl = "https://api.telegram.org/file/bot" + botConfig.getToken() + "/" + filePath;
        byte[] videoBytes;

        System.out.println(downloadUrl);
        try (InputStream in = new URL(downloadUrl).openStream()) {
            videoBytes = in.readAllBytes();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println(downloadUrl);

        if(video_handler.getRenameRequest(userId) != null){
            BotMessage botMessage = botMessageRepository.findByMessageKeyAndBot_Id(video_handler.getRenameRequest(userId).key, clientBot.getId()).get();



            // botMessage.setPhoto_updatedAt(LocalDateTime.now());
            botMessage.setVideo(videoBytes);
            botMessage.setVideoMimeType("video/mp4");


            //  photo_handler.clear_add(userId);
            botMessageRepository.save(botMessage);

            video_handler.clear(userId);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(msg.getChatId().toString());

            clientBot.setActive(false);
            botRepository.save(clientBot);

            sendMessage.setText("✅ Видео успешно обновлено");

            try {
               // bot.execute(sendMessage);

                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }
        else if(video_handler.getaddRequest(userId) != null){
            System.out.println("000000000000000000000000011111111111111111111111111111111110000000000000000");
            BotMessage botMessage = botMessageRepository.findByMessageKeyAndBot_Id(video_handler.getaddRequest(userId).key, clientBot.getId()).get();


           botMessage.setVideo(videoBytes);
           botMessage.setVideoMimeType("video/mp4");

           Bot bot1 = botRepository.findById(clientBot.getId()).get();


            // botik.getBotmessages().add(botMessage);
/*
            List<BotMessage> bm = botik.getBotmessages();
            bm.add(botMessage);
            botik.setBotmessages(bm);
*/
            //  botRepository.save(botik);

            botMessageRepository.save(botMessage);


            //    User user = new User();
            // user.setBot(botRepository.findById(photo_handler.getaddRequest(userId).getBotId()).get());
            //  user.setChatId(123L);

            //   userRepository.save(user);

            System.out.println("aaaaaaaaaaaaaaaaaaa");
            //  botRepository.save(botik);


            System.out.println(botMessage.getMessageKey() +" "+botMessage.getPhotoMimeType()+ " " + botMessage.getBot().getId());




            //  botMessageRepository.save(botMessage);
            video_handler.clear_add(userId);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(msg.getChatId().toString());

            clientBot.setActive(false);
            botRepository.save(clientBot);

            sendMessage.setText("✅ Видео успешно обновлено");

            try {
              //  bot.execute(sendMessage);

                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }


    }

}
