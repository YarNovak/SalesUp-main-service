package io.proj3ct.SpringDemoBot.DaO.MenuManaging.PodMenu_editing;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.Config.BotConfig;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Slf4j
public class Editpodmenu implements CallbackHandler {


    @Autowired
    VapecomponyRepository vapecomponyRepository;

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;


    @Autowired
    VapecomponyRepository vape;

    @Autowired
    private BotConfig config;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA AAAAAAAAAAAAAAA AAAAAAAAA A A A A A" + callbackData);
        return

                ((callbackData.startsWith("EDIT_MENU:")) || (callbackData.startsWith("EDITAGAIN:")));
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {




        String[] parts = query.getData().split(":");
        Long botId = Long.parseLong(parts[1]);

        if(!cheking.mustCheck(botId, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);

        System.out.println(botId);

        send_compony_repository(query, query.getMessage().getChatId(), botId, bot);

    }

    private void send_compony_repository(CallbackQuery query, long chatId, Long botId, TelegramLongPollingBot bot) {

        if(query.getData().startsWith("EDIT_MENU:")){
            SendPhoto message = new SendPhoto();

            message.setChatId(query.getMessage().getChatId().toString());
            message.setParseMode("MarkDownV2");

            message.setCaption(escapeMarkdown("\uD83E\uDDD1\u200D\uD83D\uDCBB")+" Нажмите на соответствующий элемент ниже\n" +
                    "\n" +"> "+
                    escapeMarkdown("\uD83C\uDFF7") + "Здесь описаны все элементы вашего каталога / меню\n" + "> "+
                    "\n" + "> "+
                    escapeMarkdown("\uD83D\uDCA1") + "Выберите именно тот раздел, который вас интересует\n" +
                    "\n" +
                    escapeMarkdown("\uD83D\uDC47\uD83D\uDC47\uD83D\uDC47"));

            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();


            long k = 0;
            List<Vapecompony> vcr = StreamSupport.stream(vapecomponyRepository.findAllByBot_Id(Long.valueOf(botId)).spliterator(), false)
                    .collect(Collectors.toList());

            while(vcr.size()>0){


                List<InlineKeyboardButton> rowInLine = new ArrayList<>();

                var yesButton = new InlineKeyboardButton();

                vcr.get(0).getName();
                yesButton.setText(vcr.get(0).getName());
                yesButton.setCallbackData("EDIT_PODMENU:"+vcr.get(0).getId()+":"+botId);
                vcr.remove(0);
                rowInLine.add(yesButton);

                rowsInLine.add(rowInLine);

            }
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();

            var yesButton = new InlineKeyboardButton();


            yesButton.setText("✚ Добавить раздел \uD83D\uDECD\uFE0F");
            yesButton.setCallbackData("PODMENU_ADD:" + botId);

            var nazad = new InlineKeyboardButton();


            nazad.setText("\uD83D\uDD19 Назад");
            nazad.setCallbackData("TOTALLY_END");



            rowInLine.add(yesButton);
            rowInLine.add(nazad);

            rowsInLine.add(rowInLine);

            markupInLine.setKeyboard(rowsInLine);
            message.setReplyMarkup(markupInLine);
            message.setPhoto(new InputFile("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/Menu.png"));

            try{

              //  bot.execute(message);

                Message msgg =  bot.execute(message);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
            }
            catch (TelegramApiException e){
                e.printStackTrace();
            }
        }
        else{

           EditMessageMedia message = new EditMessageMedia();
            message.setMessageId(query.getMessage().getMessageId());
            message.setChatId(query.getMessage().getChatId().toString());

            InputMediaPhoto newPhoto = new InputMediaPhoto();

            newPhoto.setCaption(escapeMarkdown("\uD83E\uDDD1\u200D\uD83D\uDCBB")+" Нажмите на соответствующий элемент ниже\n" +
                    "\n" +"> "+
                    escapeMarkdown("\uD83C\uDFF7") + "Здесь описаны все элементы вашего каталога / меню\n" + "> "+
                    "\n" + "> "+
                    escapeMarkdown("\uD83D\uDCA1") + "Выберите именно тот раздел, который вас интересует\n" +
                    "\n" +
                    escapeMarkdown("\uD83D\uDC47\uD83D\uDC47\uD83D\uDC47"));

            newPhoto.setParseMode("MarkDownV2");
            newPhoto.setMedia("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/Menu.png");
            message.setMedia(newPhoto);

            InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();

            List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();


            long k = 0;
            List<Vapecompony> vcr = StreamSupport.stream(vapecomponyRepository.findAllByBot_Id(Long.valueOf(botId)).spliterator(), false)
                    .collect(Collectors.toList());

            while(vcr.size()>0){


                List<InlineKeyboardButton> rowInLine = new ArrayList<>();

                var yesButton = new InlineKeyboardButton();

                vcr.get(0).getName();
                yesButton.setText(vcr.get(0).getName());
                yesButton.setCallbackData("EDIT_PODMENU:"+vcr.get(0).getId()+":"+botId);
                vcr.remove(0);
                rowInLine.add(yesButton);

                rowsInLine.add(rowInLine);

            }
            List<InlineKeyboardButton> rowInLine = new ArrayList<>();

            var yesButton = new InlineKeyboardButton();


            yesButton.setText("✚ Добавить раздел");
            yesButton.setCallbackData("PODMENU_ADD:" + botId);

            var nazad = new InlineKeyboardButton();


            nazad.setText("\uD83D\uDD19 Назад");
            nazad.setCallbackData("TOTALLY_END");



            rowInLine.add(yesButton);
            rowInLine.add(nazad);

            rowsInLine.add(rowInLine);

            markupInLine.setKeyboard(rowsInLine);
            message.setReplyMarkup(markupInLine);
            //message.setPhoto(new InputFile("https://cdn.serif.com/affinity/img/photo/home/0824/slider/photo-assets-020820240816--lg@2x.png"));

            try{

                bot.execute(message);


            }
            catch (TelegramApiException e){
                e.printStackTrace();
            }
        }




    }

    private void executeMessage(SendMessage message, TelegramLongPollingBot bot) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void verifySavedItem(Vapecompony item) {
        boolean exists = vape.existsById(item.getId());
        log.info("Item {} exists in DB: {}", item.getId(), exists);
        if (!exists) {
            log.error("Item was not saved to DB!");
        }
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
