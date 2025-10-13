package io.proj3ct.SpringDemoBot.DaO.ExelExporter;

import io.proj3ct.SpringDemoBot.DaO.CallbackHandler;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.HelpingServise.OwnerOrNo.OwnerCheking;
import io.proj3ct.SpringDemoBot.dopclasses.Exel.ExcelExportService;
import io.proj3ct.SpringDemoBot.model.*;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.ByteArrayInputStream;
import java.util.List;

@Component
public class ExelStat implements CallbackHandler {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private VapecomponyKatalogRepository katalogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private MessageRegistry messageRegistry;
    @Autowired
    private BotRepository botRepository;

    @Autowired
    private OwnerCheking cheking;

    @Override
    public boolean support(String callbackData) {
        return callbackData.startsWith("Statystyks:");
    }

    @Override
    @Transactional(readOnly = true)
    public void handle(CallbackQuery query, TelegramLongPollingBot bot) {
        String token = query.getData().replace("Statystyks:", "");

        if(!cheking.mustCheck(botRepository.findByBotToken(token).get().getId(), query.getMessage().getChatId(), bot)){return;};
        messageRegistry.deleteMessagesAfter(query.getMessage().getChatId(), query.getMessage().getMessageId(), false, bot);


        System.out.println(token);// catalog, cart, ...
        try {
            List<Orders> orders = ordersRepository.findAllByBot_BotToken(token);
            List<Vapecompony_katalog> products = katalogRepository.findAllByBot_BotToken(token);
            List<User> users = userRepository.findAllByBot_BotToken(token);

            byte[] excelData = excelExportService.exportToExcel(orders, products, users);

            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(query.getMessage().getChatId().toString());
            sendDocument.setDocument(new InputFile(new ByteArrayInputStream(excelData), "statistics.xlsx"));

       //     bot.execute(sendDocument);

            Message msgg =  bot.execute(sendDocument);
            messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
