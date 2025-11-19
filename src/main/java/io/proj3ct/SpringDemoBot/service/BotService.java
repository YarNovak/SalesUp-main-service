package io.proj3ct.SpringDemoBot.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.BotMessage;
import io.proj3ct.SpringDemoBot.DB_entities.BotMessageTextsDef;
import io.proj3ct.SpringDemoBot.DB_entities.PlatformUser;
import io.proj3ct.SpringDemoBot.DaO.BotCreating.TgTokenvalidator;
import io.proj3ct.SpringDemoBot.repository.BotDefTextRepository;
import io.proj3ct.SpringDemoBot.repository.BotMessageRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BotService {

    private final BotRepository botRepository;
    private final BotMessageRepository botMessageRepository;
    private final BotDefTextRepository botDefTextRepository;
    private final WebhookService webhookService;
    
    public Bot createBotFreeTrial(PlatformUser user, String token) {
        return createBot(user, token, "free", 
        BigDecimal.ZERO, 7, true, true, true);
    }

    public Bot createBotFreeTrial(PlatformUser user, String token, int trialPeriodDays) {
        return createBot(user, token, "free", 
        BigDecimal.ZERO, trialPeriodDays, true, true, true);
    }
 

    public Bot createBot(PlatformUser user, String token, 
                        String subscriptionStatus, 
                        BigDecimal currentPrice, 
                        int trialPeriodDays,
                        boolean active,
                        boolean cart,
                        boolean nalichka) {
        Bot botik = new Bot();
        botik.setOwner(user);
        botik.setBotToken(token);
        botik.setSubscriptionStatus(subscriptionStatus);
        botik.setCurrentPrice(currentPrice);
        botik.setRegistrationDate(LocalDateTime.now());
        botik.setPaymentDue(LocalDateTime.now().plusDays(trialPeriodDays));
        botik.setActive(active);
        botik.setBotusername(TgTokenvalidator.printBotInfo(token).getUserName());
        botik.setName(TgTokenvalidator.printBotInfo(token).getFirstName());
        botik.setCart(cart);
        botik.setNalichka(nalichka);
        //  defaultValues.setDefault(botik);
        botik .create();


        // defaultValues.setDefault(botik);
        System.out.println("[Bot] Creating bot with token from user: " + user.getTelegramId());
        botRepository.save(botik);
        // defaultValues.setDefault(botik);
        System.out.println("[Bot] Saved to repository with ID: " + botik.getId());

        generateDefaultMessagesForBot(botik);
        System.out.println("[BotMessages] Default messages generated for bot ID: " + botik.getId());

        // Register webhook for the new bot
        webhookService.registerTenantWebhook(botik.getBotToken());
        System.out.println("[Webhook] registered for bot ID: " + botik.getId());

        return botik;
    }

    // [Denys] YARIK! method need to be checked
    public void generateDefaultMessagesForBot(Bot bot) {
        try {
            List<BotMessageTextsDef> defaultMessages = botDefTextRepository.findAll();
            for (BotMessageTextsDef defText : defaultMessages) {
                var messageKey = defText.getMessageKey();
                var botId = bot.getId();
                // Чистим старе повідомлення, якщо існує
                if (botMessageRepository.existsByMessageKeyAndBot_Id(messageKey, botId)) {
                    botMessageRepository.deleteByMessageKeyAndBot_Id(messageKey, botId);
                }
                // Створюємо нове повідомлення з дефолтним текстом
                BotMessage botMessage = new BotMessage();
                botMessage.setBot(bot);
                botMessage.setMessageKey(defText.getMessageKey());
                botMessage.setText(defText.getText());
                botMessageRepository.save(botMessage);
            }
        } catch (Exception e) {
            System.err.println("Error generating default messages for bot ID " + bot.getId() + ": " + e.getMessage());
        }
    }

    public Bot findBotByToken(String token) {
        return botRepository.findByBotToken(token).orElse(null);
    }
    
    public String extractUsernameFromToken(String token) {

        Bot need_bot = botRepository.findByBotToken(token).get();
        return need_bot.getBotusername();
    }
}
