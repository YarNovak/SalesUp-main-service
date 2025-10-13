package io.proj3ct.SpringDemoBot.DaO.MessagEditing.BotFatherSettings.FatherSettingsLastShtrihs;


import io.proj3ct.SpringDemoBot.DaO.MessagEditing.BotFatherSettings.BotFatherHandlers.FatherSettings_handler;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.Change_Prod.NewLastShtrihs.ChangeOpys_handler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.config.BotConfig;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Component
public class BotName_LastShtrih implements MessageHandle {

    @Autowired
    private FatherSettings_handler fatherSettings_handler;

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(Message msgcallbackData) {

        FatherSettings_handler.RenameRequest renameRequest = fatherSettings_handler.getResetRequest(msgcallbackData.getFrom().getId());

        if(renameRequest == null) return false;

        if(msgcallbackData.hasText()){
            if ( (renameRequest.key.equals("Name") )  ||  (renameRequest.key.equals("About") ) ||  (renameRequest.key.equals("Description") ))  return true;
            return false;
        }


        if(msgcallbackData.hasPhoto()){

            if(renameRequest.key.equals("BotF_Description_picture") || (renameRequest.key.equals("BotF_Botpic"))) return true;
            return false;

        }
        if(msgcallbackData.hasAnimation() && renameRequest.key.equals("BotF_Animation_picture") || (renameRequest.key.equals("BotF_Description_picture"))) return true;

        return fatherSettings_handler.getResetRequest(msgcallbackData.getFrom().getId())!=null;
    }

    @Override
    public void handle(Message msg, TelegramLongPollingBot bot) {

        Long userId = msg.getFrom().getId();
        FatherSettings_handler.RenameRequest renameRequest = fatherSettings_handler.getResetRequest(userId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(msg.getChatId().toString());

        if(msg.hasText() ){


      //  && isValidName(msg.getText())
            if(renameRequest.key.equals("Name")){

                if(isValidName(msg.getText())){

                    sendMessage.setText("✅Название сохранено!\n\n" +
                            "Спасибо за обновление. В ближайшие несколько минут администратор синхронизирует изменения");

                    SendMessage messge_for_owner = new SendMessage();

                    messge_for_owner.setChatId("806730294");
                    messge_for_owner.setText("Для бота " + renameRequest.botId + " новий"+ renameRequest.key + "\n\n"+msg.getText());
                    fatherSettings_handler.clear(msg.getFrom().getId());
                    try{
                    //    bot.execute(messge_for_owner);


                        Message msgg2 =  bot.execute(messge_for_owner);
                       // messageRegistry.addMessage(msgg2.getChatId(), msgg2.getMessageId());


                      //  bot.execute(sendMessage);

                        Message msgg =  bot.execute(sendMessage);
                        messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                    }
                    catch (TelegramApiException e){
                        e.printStackTrace();
                    }
                }
                else{

                    sendMessage.setText("⚠\uFE0F Изменения не сохранены\n\n" +
                            "К сожалению, внесённые данные не соответствуют требованиям Telegram.\nВведите имя длиной до 64 символов:");
                    try {
                        bot.execute(sendMessage);

                        Message msgg =  bot.execute(sendMessage);
                        messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                    }
                    catch (TelegramApiException e){
                        e.printStackTrace();
                    }

                }


            }
            else if(renameRequest.key.equals("About") ){

                if(isValidAbout(msg.getText())){

                    sendMessage.setText("✅Краткое описание сохранено!\n\n" +
                            "Спасибо за обновление. В ближайшие несколько минут администратор синхронизирует изменения");

                    SendMessage messge_for_owner = new SendMessage();

                    messge_for_owner.setChatId("806730294");
                    messge_for_owner.setText("Для бота " + renameRequest.botId + " новий"+ renameRequest.key + "\n\n"+msg.getText());
                    fatherSettings_handler.clear(msg.getFrom().getId());
                    try{
                        //    bot.execute(messge_for_owner);


                        Message msgg2 =  bot.execute(messge_for_owner);
                       // messageRegistry.addMessage(msgg2.getChatId(), msgg2.getMessageId());


                        //  bot.execute(sendMessage);

                        Message msgg =  bot.execute(sendMessage);
                        messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                    }
                    catch (TelegramApiException e){
                        e.printStackTrace();
                    }
                }
                else{

                    sendMessage.setText("⚠\uFE0F Изменения не сохранены\n\n" +
                            "К сожалению, внесённые данные не соответствуют требованиям Telegram."+"\nВведите текст длиной до 120 символов:");
                    try {
                        Message msgg =  bot.execute(sendMessage);
                        messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                    }
                    catch (TelegramApiException e){
                        e.printStackTrace();
                    }

                }


            }
            else if(renameRequest.key.equals("Description")){
                if(isValidDescription(msg.getText())){

                    sendMessage.setText("✅Стартовое сообщение сохранено!\n\n" +
                            "Спасибо за обновление. В ближайшие несколько минут администратор синхронизирует изменения");

                    SendMessage messge_for_owner = new SendMessage();

                    messge_for_owner.setChatId("806730294");
                    messge_for_owner.setText("Для бота " + renameRequest.botId + " новий"+ renameRequest.key + "\n\n"+msg.getText());
                    fatherSettings_handler.clear(msg.getFrom().getId());
                    try{
                        //    bot.execute(messge_for_owner);


                        Message msgg2 =  bot.execute(messge_for_owner);
                      //  messageRegistry.addMessage(msgg2.getChatId(), msgg2.getMessageId());


                        //  bot.execute(sendMessage);

                        Message msgg =  bot.execute(sendMessage);
                        messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                    }
                    catch (TelegramApiException e){
                        e.printStackTrace();
                    }
                }
                else{

                    sendMessage.setText("⚠\uFE0F Изменения не сохранены\n\n" +
                            "К сожалению, внесённые данные не соответствуют требованиям Telegram."+"\nОписание не может превышать 512 символов (включая переносы строк):");
                    try {
                        Message msgg =  bot.execute(sendMessage);
                        messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                    }
                    catch (TelegramApiException e){
                        e.printStackTrace();
                    }

                }



            }



        }

        else if(msg.hasPhoto()){





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


            if(renameRequest.key.equals("BotF_Description_picture")){

                if(isValidDescriptionPicture(bestPhoto)){

                    sendMessage.setText("✅Фото к стартовому сообщению сохранено!\n\n" +
                            "Спасибо за обновление. В ближайшие несколько минут администратор синхронизирует изменения");

                    SendPhoto messge_for_owner = new SendPhoto();

                    messge_for_owner.setChatId("806730294");
                    messge_for_owner.setPhoto(new InputFile(bestPhoto.getFileId()));
                    messge_for_owner.setCaption("Для бота " + renameRequest.botId + " новий"+ renameRequest.key);
                    fatherSettings_handler.clear(msg.getFrom().getId());
                    try{
                        //    bot.execute(messge_for_owner);


                        Message msgg2 =  bot.execute(messge_for_owner);
                        //messageRegistry.addMessage(msgg2.getChatId(), msgg2.getMessageId());


                        //  bot.execute(sendMessage);

                        Message msgg =  bot.execute(sendMessage);
                        messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                    }
                    catch (TelegramApiException e){
                        e.printStackTrace();
                    }


                }
                else{

                    sendMessage.setText("⚠\uFE0F Изменения не сохранены\n\n" +
                            "К сожалению, внесённые данные не соответствуют требованиям Telegram."+"\n\nПожалуйста, загрузите фото для стартового сообщения размером 640×360 пикселей. Или GIF размером 320×180, 640×360 или 960×540 пикселей.\n" +
                            "Люди увидят это фото или GIF, когда откроют чат с вашим SalesUp ботом, в блоке с заголовком «Что умеет этот бот?».");
                    try {
                        Message msgg =  bot.execute(sendMessage);
                        messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                    }
                    catch (TelegramApiException e){
                        e.printStackTrace();
                    }

                }

            }
            else if(renameRequest.key.equals("BotF_Botpic")){

                sendMessage.setText("✅Аватарка сохранена!\n\n" +
                        "Спасибо за обновление. В ближайшие несколько минут администратор синхронизирует изменения");

                SendPhoto messge_for_owner = new SendPhoto();

                messge_for_owner.setChatId("806730294");
                messge_for_owner.setPhoto(new InputFile(bestPhoto.getFileId()));
                messge_for_owner.setCaption("Для бота " + renameRequest.botId + " новий"+ renameRequest.key);
                fatherSettings_handler.clear(msg.getFrom().getId());
                try{
                    //    bot.execute(messge_for_owner);


                    Message msgg2 =  bot.execute(messge_for_owner);
                  //  messageRegistry.addMessage(msgg2.getChatId(), msgg2.getMessageId());


                    //  bot.execute(sendMessage);

                    Message msgg =  bot.execute(sendMessage);
                    messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                }
                catch (TelegramApiException e){
                    e.printStackTrace();
                }

            }


        }
        else if(msg.hasAnimation()){

            if(renameRequest.key.equals("BotF_Description_picture")){

                if(isValidDescriptionGif(msg.getAnimation())){

                    sendMessage.setText("✅Фото к стартовому сообщению сохранено!\n\n" +
                            "Спасибо за обновление. В ближайшие несколько минут администратор синхронизирует изменения");

                    SendAnimation messge_for_owner = new SendAnimation();
                    messge_for_owner.setChatId("806730294");
                    messge_for_owner.setAnimation(new InputFile( msg.getAnimation().getFileId()));
                    messge_for_owner.setCaption("Для бота " + renameRequest.botId + " новий"+ renameRequest.key);
                    fatherSettings_handler.clear(msg.getFrom().getId());

                    try{
                        //    bot.execute(messge_for_owner);


                        Message msgg2 =  bot.execute(messge_for_owner);
                   //     messageRegistry.addMessage(msgg2.getChatId(), msgg2.getMessageId());


                        //  bot.execute(sendMessage);

                        Message msgg =  bot.execute(sendMessage);
                        messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                    }
                    catch (TelegramApiException e){
                        e.printStackTrace();
                    }


                }
                else{

                    sendMessage.setText("⚠\uFE0F Изменения не сохранены\n\n" +
                            "К сожалению, внесённые данные не соответствуют требованиям Telegram."+"\n\nПожалуйста, загрузите фото для стартового сообщения размером 640×360 пикселей. Или GIF размером 320×180, 640×360 или 960×540 пикселей.\n" +
                            "Люди увидят это фото или GIF, когда откроют чат с вашим SalesUp ботом, в блоке с заголовком «Что умеет этот бот?».");
                    try {

                        //  bot.execute(sendMessage);

                        Message msgg =  bot.execute(sendMessage);
                        messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                    }
                    catch (TelegramApiException e){
                        e.printStackTrace();
                    }

                }


            }
            else{

                sendMessage.setText("⚠\uFE0F Изменения не сохранены\n\n" +
                        "К сожалению, внесённые данные не соответствуют требованиям системных данных Telegram.");
                try {


                    Message msgg =  bot.execute(sendMessage);
                    messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                }
                catch (TelegramApiException e){
                    e.printStackTrace();
                }

            }

        }

        else{

            sendMessage.setText("⚠\uFE0F Изменения не сохранены\n\n" +
                    "К сожалению, внесённые данные не соответствуют требованиям системных данных Telegram.");
            try {

                //  bot.execute(sendMessage);

                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
            }
            catch (TelegramApiException e){
                e.printStackTrace();
            }

        }


    }


    private boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        int length = name.codePointCount(0, name.length()); // щоб коректно рахувало емодзі і юнікод
        return length >= 1 && length <= 64;
    }

    private boolean isValidAbout(String about) {
        if (about == null) return false;
        int length = about.codePointCount(0, about.length());
        return length <= 120;
    }

    public boolean isValidDescription(String description) {
        if (description == null) return false;
        int length = description.codePointCount(0, description.length());
        return length <= 512;
    }

    public void SendOkNotOk( FatherSettings_handler.RenameRequest renameRequest, Message message ){


        SendMessage sendMessage = new SendMessage();
        //sendMessage.setChatId(msg.getChatId().toString());


    }

    public static boolean isValidDescriptionPicture(PhotoSize photo) {

        int width = photo.getWidth();
        int height = photo.getHeight();

        return (width == 640 && height == 360);
    }

    public static boolean isValidDescriptionGif(Animation animation) {
        if (animation == null) return false;
        int width = animation.getWidth();
        int height = animation.getHeight();
        return (width == 320 && height == 180)
                || (width == 640 && height == 360)
                || (width == 960 && height == 540);
    }


}
