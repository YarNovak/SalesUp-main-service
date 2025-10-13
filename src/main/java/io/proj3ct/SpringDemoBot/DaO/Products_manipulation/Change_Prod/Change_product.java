package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.Change_Prod;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.Change_Prod.NewLastShtrihs.ChangeOpys_handler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.Change_Prod.Update_Handlers.Changename_handler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.CenaForProd_LastShtrih;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddCena_prod_handler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddKilkist_prod_handler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony_katalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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
public class Change_product implements CallbackHandler {

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private Changename_handler changenameHandler;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private AddCena_prod_handler addCenaProdHandler;

    @Autowired
    private AddKilkist_prod_handler addKilkistProdHandler;

    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("UPDATE_FORPROD_");
    }

    @Override
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {



        String[] parts = query.getData().split(":");

        Long v_kid = Long.parseLong(parts[1]);
        Long bot_id = Long.parseLong(parts[2]);

        if(!cheking.mustCheck(bot_id, query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(query.getMessage().getChatId().toString());

        Vapecompony_katalog vapecomponyKatalog = vapecomponyKatalogRepository.findByIdAndBot_Id(v_kid, bot_id).orElse(null);
        if(vapecomponyKatalog == null) {
            sendMessage.setText("Продукту не існує(");
            return;
        }

        if(parts[0].equals("UPDATE_FORPROD_NAME")){

            sendMessage.setText("\uD83D\uDCAC Введите новый текст:");
            changenameHandler.expect_namechange_product(query.getFrom().getId(), bot_id,  v_kid);

            try{

              //  bot.execute(sendMessage);
                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }

        }
        else if(parts[0].equals("UPDATE_FORPROD_COST")){

            addCenaProdHandler.expect_cena_product(query.getFrom().getId(), bot_id,  vapecomponyKatalog.getName());
            sendMessage.setText("\uD83D\uDCB8 Укажите стоимость:");

            try{

           //     bot.execute(sendMessage);
                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }

        }
        else if(parts[0].equals("UPDATE_FORPROD_COUNT")){

            addKilkistProdHandler.expect_kilkist_product(query.getFrom().getId(), bot_id,  vapecomponyKatalog.getName());
            sendMessage.setText("\uD83D\uDCCA Введите количество:");

            try{

            //    bot.execute(sendMessage);
                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }

        }
        else if(parts[0].equals("UPDATE_FORPROD_PODMENU")){

            send_compony_repository(query, query.getMessage().getChatId(), bot_id, vapecomponyKatalog, bot);

        }
        else if(parts[0].equals("UPDATE_FORPROD_DESCRIPTION")){

            //changeOpysHandler.expect_opyschange_product(query.getFrom().getId(), bot_id,  v_kid);

            EditMessageMedia sendEdit = new EditMessageMedia();
            sendEdit.setChatId(String.valueOf(query.getMessage().getChatId()));
            sendEdit.setMessageId(query.getMessage().getMessageId());

            InputMediaPhoto inputMediaPhoto = new InputMediaPhoto("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/productDS.png");
            inputMediaPhoto.setParseMode("MarkDownV2");
            inputMediaPhoto.setCaption(escapeMarkdown("✏\uFE0F") +"*_Редактирование описания_*\n" +
                    "\n" +
                    escapeMarkdown("⚙\uFE0F")+" модифицируйте в любой момент");

            sendEdit.setMedia(inputMediaPhoto);

           // sendEdit.setText("Введи своє бачення даної менюшки:");

            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            keyboard.add(List.of(button("\uD83D\uDD04 ПО УМОЛЧАНИЮ", "SET_PRODDESC_DEFAULT:" + v_kid + ":" + bot_id)));
            keyboard.add(List.of(button("\uD83D\uDCDD Изменить", "SET_PRODDESC_CHNG:"+ v_kid + ":" + bot_id)));
            keyboard.add(List.of(button("\uD83D\uDD19 Назад", "AGAIN_UPDATE:"+ v_kid + ":" + bot_id)));


            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboard);
            sendEdit.setReplyMarkup(markup);
            try{

                bot.execute(sendEdit);

            }
            catch(TelegramApiException e){
                e.printStackTrace();
            }

        }


    }

    private void send_compony_repository(CallbackQuery query, long chatId, Long botId, Vapecompony_katalog vapecompony_katalog, TelegramLongPollingBot bot) {

        EditMessageMedia message = new EditMessageMedia();

        message.setMessageId(query.getMessage().getMessageId());
        message.setChatId(String.valueOf(chatId));

        InputMediaPhoto photo = new InputMediaPhoto("https://raw.githubusercontent.com/YarNovak/BOT12500Photos/refs/heads/main/Menu.png");
        photo.setCaption("\uD83D\uDC68\u200D\uD83D\uDCBBВыбери где будет находиться этот элемент\n" +
                "\n" +
                "\uD83D\uDC47\uD83D\uDC47\uD83D\uDC47");
        message.setMedia(photo);

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
            yesButton.setCallbackData("ADD_HERE:"+vapecompony_katalog.getId()+":"+vcr.get(0).getId()+":"+botId);
            vcr.remove(0);
            rowInLine.add(yesButton);

            rowsInLine.add(rowInLine);

        }

        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        var yesButton = new InlineKeyboardButton();

        yesButton.setText("\uD83D\uDD19 Назад");
        yesButton.setCallbackData("AGAIN_UPDATE:"+vapecompony_katalog.getId() + ":" + botId);
        rowInLine.add(yesButton);

        rowsInLine.add(rowInLine);

      //  keyboard.add(List.of(button("Назад", "AGAIN_UPDATE:"+ v_kid + ":" + botId)));


        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        try {
            bot.execute(message);
        }
        catch(TelegramApiException e){
            e.printStackTrace();
        }
    }

    private void executeMessage(SendMessage message, TelegramLongPollingBot bot) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private InlineKeyboardButton button(String text, String key) {

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(key);
        return button;
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
