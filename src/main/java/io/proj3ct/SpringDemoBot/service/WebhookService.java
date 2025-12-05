package io.proj3ct.SpringDemoBot.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.math3.analysis.function.Abs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.google.api.client.util.Value;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.repository.BotMessageRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebhookService {

    @Value("${app.ngrok}")
    private final String baseUrl = "https://unscaling-trembly-roseanna.ngrok-free.dev"; // Replace with your actual base URL

    @Autowired
    private BotRepository botRepository;

    @PostConstruct
    public void init() {
        System.out.println("Init: Register Webhooks...");
        List<Bot> bots = botRepository.findAll();
        for (Bot bot : bots) {
            registerTenantWebhook(bot.getBotToken(), baseUrl);
        }
    }

    public void registerTenantWebhook(String tenantBotToken) {
        System.out.println("baseUrl: " + baseUrl);
        registerTenantWebhook(tenantBotToken, baseUrl);
    }

    /**
     * Call this method when you add a new tenant (bot) to your system.
     *
     * @param tenantBotToken The bot Token for the new tenant
     * @param yourBaseUrl    The public base URL of your application (e.g., "https://your-app.com")
     */
    public void registerTenantWebhook(String tenantBotToken, String yourBaseUrl) {
        try {
            System.out.println("Process: Registering webhook for tenant bot token: " + tenantBotToken);
            Bot bot = botRepository.findByBotToken(tenantBotToken).orElseThrow(() -> new IllegalArgumentException("Bot with the provided token does not exist."));
            Long botId = bot.getId();
            // 1. Create a new, temporary sender using the TENANT'S token
            AbsSender sender = new DefaultAbsSender(new DefaultBotOptions()) {
                @Override
                public String getBotToken() {
                    return tenantBotToken;
                }
            };
            // Create the unique webhook URL
            String webhookUrl = yourBaseUrl + "/webhook/" + botId;
            System.out.println("Webhook string: " + webhookUrl);

            // Create the SetWebhook method
            SetWebhook setWebhook = SetWebhook.builder()
                    .url(webhookUrl)
                    .build();

            // Execute the method
            sender.execute(setWebhook);
            System.out.println("Webhook registered for Bot UUID (ending in): " + botId);

        } catch (TelegramApiException e) {
            System.err.println("Failed to register webhook for tenant: " + e.getMessage());
            // Handle exception (e.g., log it, notify admin)
        }
    }

    /**
     * Call this method when you remove a tenant (bot) from your system.
     *
     * @param tenantBotToken The bot Token of the tenant to remove
     */
    public void deleteTenantWebhook(String tenantBotToken) {
        try {
            Bot bot = botRepository.findByBotToken(tenantBotToken).orElseThrow(() -> new IllegalArgumentException("Bot with the provided token does not exist."));
            Long botId = bot.getId();

            AbsSender sender = new DefaultAbsSender(new DefaultBotOptions()) {
                @Override
                public String getBotToken() {
                    return tenantBotToken;
                }
            };

            // Create the DeleteWebhook method
            DeleteWebhook deleteWebhook = DeleteWebhook.builder().build();

            // Execute the method
            sender.execute(deleteWebhook);
            System.out.println("Webhook deleted for Bot UUID (ending in): " + botId);

        } catch (TelegramApiException e) {
            System.err.println("Failed to delete webhook for tenant: " + e.getMessage());
        }
    }
}
