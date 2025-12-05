package io.proj3ct.SpringDemoBot.DaO.MenuManaging.PodMenu_changeszczegoly.Media.PhotoM;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.MenuManaging.HEPLING_SERVICES.Photos.MenuPhoto_handler;
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
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Component
public class PhotoMenu_LastShtrih implements MessageHandle {

    @Autowired
    private MenuPhoto_handler menuPhotoHandler;
    @Autowired
    private BotRepository botRepository;
    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private BotConfig botConfig;
    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(org.telegram.telegrambots.meta.api.objects.Message msg) {

            return ((menuPhotoHandler.getRenameRequest(msg.getFrom().getId())   != null) ||  (menuPhotoHandler.getaddRequest(msg.getFrom().getId()) != null ));

    }

    @Override
    public void handle(Message msg, TelegramLongPollingBot bot) {
        Long userId = msg.getFrom().getId();
        MenuPhoto_handler.RenameRequest request = menuPhotoHandler.getRenameRequest(userId);

        if (request == null) {

            request = menuPhotoHandler.getaddRequest(userId);


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

        // Скачати фото
        String downloadUrl = "https://api.telegram.org/file/bot" + botConfig.getToken() + "/" + filePath;
        byte[] photoBytes;

        try (InputStream in = new URL(downloadUrl).openStream()) {
            photoBytes = in.readAllBytes();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        if(menuPhotoHandler.getRenameRequest(userId) != null) {

            Vapecompony vapecompony = vapecomponyRepository.findByIdAndBot_Id(menuPhotoHandler.getRenameRequest(userId).key, clientBot.getId()).get();
            vapecompony.setPhoto(photoBytes);
            vapecompony.setPhotoMimeType("image/jpeg");

            vapecomponyRepository.save(vapecompony);

            menuPhotoHandler.clear(userId);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(msg.getChatId().toString());
            sendMessage.setText("✅ Фото успешно обновлено");

            clientBot.setActive(false);
            botRepository.save(clientBot);

            try {
            //    bot.execute(sendMessage);


                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }

        else if(menuPhotoHandler.getaddRequest(userId) != null) {

            Vapecompony vapecompony = vapecomponyRepository.findByIdAndBot_Id(menuPhotoHandler.getaddRequest(userId).key, clientBot.getId()).get();

            vapecompony.setPhoto(photoBytes);
            vapecompony.setPhotoMimeType("image/jpeg");

            vapecomponyRepository.save(vapecompony);
            clientBot.setActive(false);
            botRepository.save(clientBot);

            menuPhotoHandler.clear_add(userId);
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
