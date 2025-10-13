package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs;

import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddKilkist_prod_handler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.model.Vapecompony;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.VapecomponyRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony_katalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class KilkistForProd_LastShtrih implements MessageHandle {

    @Autowired
    private AddKilkist_prod_handler addKilkist_prod_handler;

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Override
    public boolean support(Message msgcallbackData) {
        return (addKilkist_prod_handler.get_kilkist_product(msgcallbackData.getFrom().getId()) != null);
    }

    @Override
    public void handle(Message msg, TelegramLongPollingBot bot) {
        Long userId = msg.getFrom().getId();
        AddKilkist_prod_handler.RenameRequest request = addKilkist_prod_handler.get_kilkist_product(userId);
        Long bot_Id = request.botId;


        if(isPositiveInteger(msg.getText())){

            Vapecompony_katalog v_k = vapecomponyKatalogRepository.findByNameAndBot_Id(request.key, request.botId).get();
            v_k.setKilkist(Long.valueOf(msg.getText()));

            vapecomponyKatalogRepository.save(v_k);
            addKilkist_prod_handler.clear(userId);


            if(v_k.getVapecompony() == null) send_compony_repository(msg.getChatId(), bot_Id, v_k, bot);
            else {

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(msg.getChatId().toString());
                sendMessage.setText("✅ Количество изменено");
                try{
                   // bot.execute(sendMessage);

                    Message msgg =  bot.execute(sendMessage);
                    messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
                }
                catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }


        }
        else{


            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(msg.getChatId().toString());
            sendMessage.setParseMode("HTML");
            sendMessage.setText("\uD83D\uDCCA Введите количество\n\n" +
                    "<i>Только целое число больше 0, без пробелов и других символов — например: 1, 5, 20</i>");

            try{
              //  bot.execute(sendMessage);

                Message msgg =  bot.execute(sendMessage);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch (TelegramApiException e){
                e.printStackTrace();
            }
        }


    }
    private boolean isPositiveInteger(String message) {
        if (message == null) return false;
        // Тільки цифри, без нулів на початку, крім випадку "0" (який ми потім відкинемо)
        if (!message.matches("[1-9]\\d*")) return false;
        return true;
    }

    private void send_compony_repository(long chatId, Long botId, Vapecompony_katalog vapecompony_katalog, TelegramLongPollingBot bot) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("\uD83D\uDC68\u200D\uD83D\uDCBBвыбери где будет находиться этот элемент\n" +
                "\n" +
                "\uD83D\uDC47\uD83D\uDC47\uD83D\uDC47");

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


        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        executeMessage(message, bot);
    }

    private void executeMessage(SendMessage message, TelegramLongPollingBot bot) {
        try {
          //  bot.execute(message);

            Message msgg =  bot.execute(message);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
