package io.proj3ct.SpringDemoBot.HelpingServise.CleanBot;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.BotMessage;
import io.proj3ct.SpringDemoBot.DB_entities.BotMessageTextsDef;
import io.proj3ct.SpringDemoBot.model.*;
import io.proj3ct.SpringDemoBot.repository.BotDefTextRepository;
import io.proj3ct.SpringDemoBot.repository.BotMessageRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.service.WebhookService;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CleanTheBot {


    @Autowired
    private BotRepository botRepository;
    @Autowired
    private FinalItemRepository finalItemRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private VapecomponyKatalogRepository vapecomponyKatalogRepository;
    @Autowired
    private VapecomponyRepository vapecomponyRepository;

    @Autowired
    private BotDefTextRepository botDefTextRepository;

    @Autowired
    private BotMessageRepository botMessageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebhookService webhookService;

    public void base_settingsForBot(Bot bot){

        System.out.println("JIJ");

        webhookService.deleteTenantWebhook(bot.getBotToken());
        

        finalItemRepository.deleteBulkByBotId(bot.getId());
        ordersRepository.deleteBulkByBotId(bot.getId());
        cartItemRepository.deleteBulkByBotId(bot.getId());
        vapecomponyKatalogRepository.deleteBulkByBotId(bot.getId());
        vapecomponyRepository.deleteBulkByBotId(bot.getId());
        userRepository.deleteBulkByBotId(bot.getId());

        if (bot.isThirdPartyToken()) {
            botMessageRepository.deleteBulkByBotId(bot.getId());
            if (bot.getButtonTexts() != null) {
                bot.getButtonTexts().clear();
            }
            botRepository.delete(bot);
            System.out.println("Third-party Bot DELETED");
            return;
        }

        System.out.println("JIJ");

        bot.create();
        bot.setOwner(null);
        bot.setPaymentDue(null);
        bot.setRegistrationDate(null);
        bot.setSubscriptionStatus(null);
        bot.setCart(true);
        bot.setNalichka(true);

        botRepository.save(bot);

       for(BotMessageTextsDef btf : botDefTextRepository.findAll()){

        Optional<BotMessage> botM = botMessageRepository.findByMessageKeyAndBot_Id(btf.getMessageKey(), bot.getId());

        if(botM.isEmpty()) continue;

        BotMessage botMessage = botM.get();

           botMessage.setText(btf.getText());
           botMessage.setEntitiesJson(btf.getEntitiesJson());

           botMessage.setVideo(null);
           botMessage.setPhoto(null);
           botMessage.setPhotoMimeType(null);
           botMessage.setPhoto_updatedAt(null);
           botMessage.setVideoMimeType(null);
           botMessage.setVideo_updatedAt(null);
           Bot bot1 = botRepository.findById(botMessage.getBot().getId()).orElse(null);
           if(bot1 != null){

               bot1.setActive(false);
               botRepository.save(bot1);
           }

           botMessageRepository.save(botMessage);


       }

        System.out.println("DONAT");

    }
}
