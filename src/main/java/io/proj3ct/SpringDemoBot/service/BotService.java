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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BotService {

    private final BotRepository botRepository;
    private final BotMessageRepository botMessageRepository;
    private final BotDefTextRepository botDefTextRepository;
    private final WebhookService webhookService;
    
    public Optional<Bot> createBotFreeTrial(PlatformUser user, String token, boolean thirdPartyToken) {
        return createBot(user, token, thirdPartyToken, "free", 
        BigDecimal.ZERO, 7, false, true, true);
    }

    public Optional<Bot> createBotFreeTrial(PlatformUser user, String token, boolean thirdPartyToken, int trialPeriodDays) {
        return createBot(user, token, thirdPartyToken, "free", 
        BigDecimal.ZERO, trialPeriodDays, false, true, true);
    }
 

    public Optional<Bot> createBot(PlatformUser user, String token,
                        boolean thirdPartyToken,
                        String subscriptionStatus, 
                        BigDecimal currentPrice, 
                        int trialPeriodDays,
                        boolean active,
                        boolean cart,
                        boolean nalichka) {
        if (findBotByToken(token) != null) {
            return Optional.empty();
        }
        Bot botik = new Bot();
        botik.setOwner(user);
        botik.setBotToken(token);
        botik.setThirdPartyToken(thirdPartyToken);
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

        return Optional.of(botik);
    }

    // Метод для загрузки партии "спящих" платформенных ботов (не от имени пользователя, без регистрации вебхука)
    public OnboardResult onboardBots(List<String> tokens) {
        List<Bot> created = new ArrayList<>();
        List<String> existed = new ArrayList<>();
        List<String> notValid = new ArrayList<>();

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (findBotByToken(token) != null) {
                // already exists in DB
                existed.add(token);
                continue;
            }
            if (!TgTokenvalidator.isValidTelegramToken(token)) {
                // token is not a valid Telegram token
                notValid.add(token);
                continue;
            }
            Bot botik = new Bot();
            botik.setOwner(null); // платформенный бот, не привязан к пользователю
            botik.setBotToken(token);
            botik.setThirdPartyToken(false);
            botik.setSubscriptionStatus(null);
            botik.setCurrentPrice(BigDecimal.ZERO);
            botik.setRegistrationDate(null);
            botik.setPaymentDue(null);
            botik.setActive(false); // "спящий" бот — не активен
            var info = TgTokenvalidator.printBotInfo(token);
            botik.setBotusername(info.getUserName());
            botik.setName(info.getFirstName());
            botik.create();
            botRepository.save(botik);
            // Генерируем дефолтные сообщения, чтобы бот был готов к активации позже
            generateDefaultMessagesForBot(botik);
            // Не регистрируем webhook для спящих ботов
            created.add(botik);
        }

        return new OnboardResult(created, existed, notValid);
    }

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
                botMessage.setEntitiesJson(defText.getEntitiesJson());
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
