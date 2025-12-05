package io.proj3ct.SpringDemoBot.DaO.MenuManaging.PodMenu_changeszczegoly.Media.VideoM;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Videos.MenuVideo_handler;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.Config.BotConfig;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
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
public class VideoMenu_LastShtrih implements MessageHandle {


    @Autowired
    private MenuVideo_handler menuVideoHandler;
    @Autowired
    private BotConfig botConfig;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(org.telegram.telegrambots.meta.api.objects.Message msg) {
        return ( (menuVideoHandler.getRenameRequest(msg.getFrom().getId()) != null) || (menuVideoHandler.getaddRequest(msg.getFrom().getId()) != null) );
    }

    @Override
    public void handle(Message msg, TelegramLongPollingBot bot) {

        System.out.println("VIDEO: " + msg.getVideo());
        System.out.println("FILE_ID: " + msg.getVideo().getFileId());
        System.out.println("BOT TOKEN: " + botConfig.getToken());

        Long userId = msg.getFrom().getId();
        MenuVideo_handler.RenameRequest request = menuVideoHandler.getRenameRequest(userId);

        if(request == null){

            request = menuVideoHandler.getaddRequest(userId);

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

              //  bot.execute(message);


                Message msgg =  bot.execute(message);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

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


        if(menuVideoHandler.getRenameRequest(userId) != null){

            Vapecompony vp = vapecomponyRepository.findByIdAndBot_Id(menuVideoHandler.getRenameRequest(userId).key, clientBot.getId()).get();

            vp.setVideo(videoBytes);
            vp.setVideoMimeType("video/mp4");

            vapecomponyRepository.save(vp);

            menuVideoHandler.clear(userId);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(msg.getChatId().toString());

            clientBot.setActive(false);
            botRepository.save(clientBot);

            sendMessage.setText("✅ Видео успешно обновлено");

            try {
                //bot.execute(sendMessage);

                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        else if(menuVideoHandler.getaddRequest(userId) != null){


            Vapecompony vp = vapecomponyRepository.findByIdAndBot_Id(menuVideoHandler.getaddRequest(userId).key, clientBot.getId()).get();

            vp.setVideo(videoBytes);
            vp.setVideoMimeType("video/mp4");

            vapecomponyRepository.save(vp);
            menuVideoHandler.clear_add(userId);

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
