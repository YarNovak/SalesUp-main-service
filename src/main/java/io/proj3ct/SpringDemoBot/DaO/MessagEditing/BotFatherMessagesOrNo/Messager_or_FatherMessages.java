package io.proj3ct.SpringDemoBot.DaO.MessagEditing.BotFatherMessagesOrNo;


import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.InputStream;

@Component
public class Messager_or_FatherMessages implements CallbackHandler {

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return ((callbackData.startsWith("EDIT_MESSAGES:")) || callbackData.startsWith("GO_BACK_FROM_EDIT:"));
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {




        String botId;
        if(query.getData().startsWith("EDIT_MESSAGES:")) botId = callbackDataPart(query.getData(), "EDIT_MESSAGES:");
        else botId = callbackDataPart(query.getData(), "GO_BACK_FROM_EDIT:");

        if(!cheking.mustCheck(Long.parseLong(botId), query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        String text =
                "_*"+"Данные профиля* — это  имя, аватарка, описание и т"+escapeMarkdown(".")+"д"+escapeMarkdown(".")+"_\n" +
                "\n" +
                "_*"+"SalesUp сообщения* — уведомления для бизнеса прямо в чате"+escapeMarkdown(".")+"_\n" +
                "\n" +
                "> P"+escapeMarkdown(".")+"S экспериментируйте и настраивайте приложение под свои задачи без лишних переходов";

        Optional<Bot> botOpt = botRepository.findById(Long.parseLong(botId));
        if (botOpt.isEmpty()) return;

        if(query.getData().startsWith("EDIT_MESSAGES:")){
            SendPhoto sendMessage = new SendPhoto();
            sendMessage.setParseMode("MarkDownV2");

            sendMessage.setChatId(query.getMessage().getChatId().toString());

           // File file = new File("src/main/resources/photos/messsages_deciding.jpg");

        //   sendMessage.setPhoto(new InputFile("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png"));


            sendMessage.setCaption(text);

            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

            keyboard.add(List.of(button("\uD83D\uDCACSalesUp сообщения", "ZMINA_MY_MESSAGES:" + botId)));
            keyboard.add(List.of(button("\uD83D\uDC64Данные профиля", "ZMINA_BOTFATHER:" + botId)));
            keyboard.add(List.of(button("🔙 Назад", "TOTALLY_END")));

            sendMessage.setReplyMarkup(new InlineKeyboardMarkup(keyboard));

            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("photos/messsages_deciding.jpg")){

              // bot.execute(sendMessage);

                InputFile inputFile = new InputFile(inputStream, "messsages_deciding.jpg");
                sendMessage.setPhoto(inputFile);
                Message msgg =  bot.execute(sendMessage);

                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                System.out.println("PIPOPA");
            }
            catch (TelegramApiException e){
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        else{
            EditMessageMedia sendMessage = new EditMessageMedia();

            sendMessage.setMessageId(query.getMessage().getMessageId());
            sendMessage.setChatId(query.getMessage().getChatId().toString());

            InputMediaPhoto newPhoto = new InputMediaPhoto();newPhoto.setParseMode(
                    "MarkDownV2"
            );

            newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/messsages_deciding.jpg");


            newPhoto.setCaption(text);


            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

            keyboard.add(List.of(button("\uD83D\uDCACSalesUp сообщения", "ZMINA_MY_MESSAGES:" + botId)));
            keyboard.add(List.of(button("\uD83D\uDC64Данные профиля", "ZMINA_BOTFATHER:" + botId)));
            keyboard.add(List.of(button("🔙 Назад", "TOTALLY_END")));

            sendMessage.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
            sendMessage.setMedia(newPhoto);

            try{
                bot.execute(sendMessage);

            }
            catch (TelegramApiException e){
                e.printStackTrace();
            }

        }



    }

    private String callbackDataPart(String full, String prefix) {
        return full.substring(prefix.length());
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        InlineKeyboardButton b = new InlineKeyboardButton(text);
        b.setCallbackData(callbackData);
        return b;
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
