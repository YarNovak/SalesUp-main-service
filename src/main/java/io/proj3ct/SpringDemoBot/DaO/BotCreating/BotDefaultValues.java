package io.proj3ct.SpringDemoBot.DaO.BotCreating;


import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.BotMessage;
import io.proj3ct.SpringDemoBot.DB_entities.BotSettings;
import io.proj3ct.SpringDemoBot.repository.BotMessageRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BotDefaultValues {

    @Autowired
    private BotRepository botRepository;
    @Autowired
    private BotMessageRepository botMessageRepository;



    public void setDefault(Bot bot) {
        BotMessage botMessage1 = new BotMessage();
        Long botId = bot.getId();

        BotMessage bt = new BotMessage();
        bt.setBot(bot);
        bt.setMessageKey("greeting");
        bt.setText("ПРИВЕЕЕЕЕЕЕТ");

       botMessageRepository.save(bt);
/*

        botMessageRepository.save(new BotMessage("greeting", "Hello Bot!", bot));
        botMessageRepository.save(new BotMessage("clearing", "Hello Bot!", bot));
        botMessageRepository.save(new BotMessage("changing", "Hello Bot!", bot));
        botMessageRepository.save(new BotMessage("payment_text", "Hello Bot!", bot));
        botMessageRepository.save(new BotMessage("delivery", "Hello Bot!", bot));
        botMessageRepository.save(new BotMessage("emoji", "Hello Bot!", bot));
        botMessageRepository.save(new BotMessage("cart", "Hello Bot!", bot));
        botMessageRepository.save(new BotMessage("adding", "Hello Bot!", bot));
        botMessageRepository.save(new BotMessage("after", "Hello Bot!", bot));
        botMessageRepository.save(new BotMessage("product", "Hello Bot!", bot));
        botMessageRepository.save(new BotMessage("menu", "Hello Bot!", bot));
        botMessageRepository.save(new BotMessage("phone", "Hello Bot!", bot));
*/


        bot.getButtonTexts().put("catalog", "🛍Каталог");
        bot.getButtonTexts().put("cart", "\uD83D\uDED2Корзина");
        bot.getButtonTexts().put("order", "Замовлення");
        bot.getButtonTexts().put("info", "Інформація");
        bot.getButtonTexts().put("contact", "Контакти");
        bot.getButtonTexts().put("add_first", "бомбический выбор");
        bot.getButtonTexts().put("add", "+");
        bot.getButtonTexts().put("delete", "-");
        bot.getButtonTexts().put("change", "\uD83D\uDCB8Оплата");
        bot.getButtonTexts().put("clear", "Очистить");

    }





}
