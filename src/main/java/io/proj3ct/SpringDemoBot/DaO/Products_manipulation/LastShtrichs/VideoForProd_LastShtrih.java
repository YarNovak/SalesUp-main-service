package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddProductPhoto_hadnler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddProductVideo_handler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony_katalog;
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
public class VideoForProd_LastShtrih implements MessageHandle {

    @Autowired
    private AddProductVideo_handler addProductVideo_handler;

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(org.telegram.telegrambots.meta.api.objects.Message msg) {
        return  ((addProductVideo_handler.getRenameRequest(msg.getFrom().getId()) != null) || (addProductVideo_handler.getaddRequest(msg.getFrom().getId()) != null)) ;

    }

    @Override
    public void handle(Message msg, TelegramLongPollingBot bot) {

        System.out.println("VIDEO: " + msg.getVideo());
        System.out.println("FILE_ID: " + msg.getVideo().getFileId());
        System.out.println("BOT TOKEN: " + botConfig.getToken());

        Long userId = msg.getFrom().getId();
        AddProductVideo_handler.RenameRequest request = addProductVideo_handler.getRenameRequest(msg.getFrom().getId());

        if(request == null){

            request = addProductVideo_handler.getaddRequest(userId);

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

               // bot.execute(message);

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


        if(addProductVideo_handler.getRenameRequest(userId) != null){

            Vapecompony_katalog vapecompony_katalog = vapecomponyKatalogRepository.findByIdAndBot_Id(addProductVideo_handler.getRenameRequest(userId).key, clientBot.getId()).get();

            vapecompony_katalog.setVideo(videoBytes);
            vapecompony_katalog.setVideoMimeType("video/mp4");

            vapecomponyKatalogRepository.save(vapecompony_katalog);

            addProductVideo_handler.clear(userId);

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
        else if(addProductVideo_handler.getaddRequest(userId) != null){

            Vapecompony_katalog vapecompony_katalog = vapecomponyKatalogRepository.findByIdAndBot_Id(addProductVideo_handler.getaddRequest(userId).key, clientBot.getId()).get();

            vapecompony_katalog.setVideo(videoBytes);
            vapecompony_katalog.setVideoMimeType("video/mp4");

            vapecomponyKatalogRepository.save(vapecompony_katalog);
            addProductVideo_handler.clear_add(userId);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(msg.getChatId().toString());

            clientBot.setActive(false);
            botRepository.save(clientBot);

            sendMessage.setText("✅ Видео успешно обновлено");

            try {
            //    bot.execute(sendMessage);

                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }

    }
}
