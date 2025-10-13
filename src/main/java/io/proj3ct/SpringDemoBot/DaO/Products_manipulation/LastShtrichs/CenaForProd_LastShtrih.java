package io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs;


import io.proj3ct.SpringDemoBot.DaO.MessageHandle;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddCena_prod_handler;
import io.proj3ct.SpringDemoBot.DaO.Products_manipulation.LastShtrichs.HandlerForProducts.AddKilkist_prod_handler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.model.VapecomponyKatalogRepository;
import io.proj3ct.SpringDemoBot.model.Vapecompony_katalog;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class CenaForProd_LastShtrih implements MessageHandle {

    @Autowired
    private AddCena_prod_handler addCenaProdHandler;

    @Autowired
    private MessageRegistry messageRegistry;

    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;

    @Autowired
    private AddKilkist_prod_handler addKilkistProd_handler;




    @Override
    public boolean support(Message msgcallbackData) {

        return (addCenaProdHandler.get_cena_product(msgcallbackData.getFrom().getId())!=null);
    }

    @Override
    public void handle(Message msg, TelegramLongPollingBot bot) {

        Long userId = msg.getFrom().getId();
        AddCena_prod_handler.RenameRequest request = addCenaProdHandler.get_cena_product(userId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(msg.getChatId().toString());

        if(isValidNumber(msg.getText())){

            Vapecompony_katalog v_k = vapecomponyKatalogRepository.findByNameAndBot_Id(request.key, request.botId).get();
            v_k.setCena(Long.valueOf(msg.getText()));

            vapecomponyKatalogRepository.save(v_k);

            if(v_k.getKilkist() == null){

                addKilkistProd_handler.expect_kilkist_product(msg.getFrom().getId(), request.botId, request.key);
                addCenaProdHandler.clear(msg.getFrom().getId());

                sendMessage.setText("\uD83D\uDCCA Введите количество:");

            }
            else {

                addCenaProdHandler.clear(msg.getFrom().getId());
                sendMessage.setText("✅ Цена обновлена");

            }




        }
        else {
            sendMessage.setParseMode("HTML");
            sendMessage.setText("\uD83D\uDCB8 Введите цену обычным числом\n\n" +
                    "<i>Без пробелов и символов, кроме запятой (,) или точки (.) — например: 199, 199.99 или 199,99</i>");

        }
        try{

           // bot.execute(sendMessage);

            Message msgg =  bot.execute(sendMessage);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

        }
        catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
    public boolean isValidNumber(String message) {
        if (message == null) return false;

        // Регулярка:
        // ^-?                  → необов'язковий мінус
        // (0([.,]\d+)?         → або 0 і необов'язкова дробова частина
        // |[1-9]\d*([.,]\d+)?  → або число без провідних нулів з необов'язковою дробовою частиною
        // )$                   → кінець рядка
        String regex = "^-?(0([.,]\\d+)?|[1-9]\\d*([.,]\\d+)?)$";
        return message.matches(regex);
    }

}
