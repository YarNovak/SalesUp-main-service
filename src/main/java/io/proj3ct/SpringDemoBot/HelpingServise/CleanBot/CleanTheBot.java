package io.proj3ct.SpringDemoBot.HelpingServise.CleanBot;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.BotMessage;
import io.proj3ct.SpringDemoBot.DB_entities.BotMessageTextsDef;
import io.proj3ct.SpringDemoBot.model.*;
import io.proj3ct.SpringDemoBot.repository.BotDefTextRepository;
import io.proj3ct.SpringDemoBot.repository.BotMessageRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
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

    public void base_settingsForBot(Bot bot){

        System.out.println("JIJ");
        bot.create();
        bot.setOwner(null);
        bot.setPaymentDue(null);
        bot.setRegistrationDate(null);
        bot.setSubscriptionStatus(null);
        bot.setCart(true);
        bot.setNalichka(true);

        botRepository.save(bot);
        System.out.println("JIJ");

        List<FinalItem> fn = finalItemRepository.findAllByBot_Id(bot.getId());

        for(FinalItem fi : fn){
            finalItemRepository.delete(fi);
        }

        List<Orders> or = ordersRepository.findAllByBot_Id(bot.getId());
        for(Orders o : or){
            ordersRepository.delete(o);
        }
        List<CartItem> ct = cartItemRepository.findAllByBot_Id(bot.getId());
        for (CartItem ci : ct) {
            cartItemRepository.delete(ci);
        }
        List<Vapecompony_katalog> vpk = vapecomponyKatalogRepository.findAllByBot_Id(bot.getId());
        for(Vapecompony_katalog vk : vpk){
            vapecomponyKatalogRepository.delete(vk);
        }
        List<Vapecompony> vp = vapecomponyRepository.findAllByBot_Id(bot.getId());
        for (Vapecompony v : vp) {
            vapecomponyRepository.delete(v);
        }
        List<User> us = userRepository.findAllByBot_Id(bot.getId());
        for (User v : us) {
           userRepository.delete(v);
        }


        //  ordersRepository.findAll();
        //    cartItemRepository.findAll();
        //
        //   vapecomponyKatalogRepository.findAll();
        //   vapecomponyRepository.findAll();

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
