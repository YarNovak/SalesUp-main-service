package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddProductPhoto_hadnler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony_katalog;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.checkerframework.checker.units.qual.A;
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
public class PhotoForProd_LastShtrih implements MessageHandle {

    @Autowired
    private AddProductPhoto_hadnler addProductPhoto_hadnler;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Override
    public boolean support(org.telegram.telegrambots.meta.api.objects.Message msg) {
        return ( (addProductPhoto_hadnler.getRenameRequest(msg.getFrom().getId()) !=null ) || (addProductPhoto_hadnler.getaddRequest(msg.getFrom().getId())!=null)  );
    }

    @Override
    public void handle(Message msg, TelegramLongPollingBot bot) {

        Long userId = msg.getFrom().getId();
        AddProductPhoto_hadnler.RenameRequest request = addProductPhoto_hadnler.getRenameRequest(userId);

        if (request == null) {

            request = addProductPhoto_hadnler.getaddRequest(userId);


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

        if(addProductPhoto_hadnler.getRenameRequest(userId) != null) {

            Vapecompony_katalog vapecompony_katalog = vapecomponyKatalogRepository.findByIdAndBot_Id(addProductPhoto_hadnler.getRenameRequest(userId).key, clientBot.getId()).get();
            vapecompony_katalog.setPhoto(photoBytes);
            vapecompony_katalog.setPhotoMimeType("image/jpeg");

            vapecomponyKatalogRepository.save(vapecompony_katalog);
            addProductPhoto_hadnler.clear(userId);

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

        else if(addProductPhoto_hadnler.getaddRequest(userId) != null) {

            Vapecompony_katalog vapecompony_katalog = vapecomponyKatalogRepository.findByIdAndBot_Id(addProductPhoto_hadnler.getaddRequest(userId).key, clientBot.getId()).get();
            vapecompony_katalog.setPhoto(photoBytes);
            vapecompony_katalog.setPhotoMimeType("image/jpeg");

            vapecomponyKatalogRepository.save(vapecompony_katalog);

            addProductPhoto_hadnler.clear_add(userId);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(msg.getChatId().toString());
            sendMessage.setText("✅ Фото успешно обновлено");
            try {
                //bot.execute(sendMessage);

                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
