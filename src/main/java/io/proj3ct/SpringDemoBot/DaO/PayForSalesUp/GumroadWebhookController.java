package io.proj3ct.SpringDemoBot.DaO.PayForSalesUp;

import io.proj3ct.SpringDemoBot.DB_entities.PlatformUser;
import io.proj3ct.SpringDemoBot.DaO.PayForSalesUp.PaymentDuties.Obrobka;
import io.proj3ct.SpringDemoBot.repository.PlatformUserRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class GumroadWebhookController {

    private final PlatformUserRepository platformUserRepository;
    private final TelegramLongPollingBot bot;
    private final Obrobka obrobka;

    public GumroadWebhookController(PlatformUserRepository platformUserRepository,
                                    TelegramLongPollingBot bot, Obrobka obrobka) {
        this.platformUserRepository = platformUserRepository;
        this.bot = bot;
        this.obrobka = obrobka;
    }

    @PostMapping("/gumroad-webhook")
    public ResponseEntity<String> handleGumroadWebhook(HttpServletRequest request) throws Exception {
        String payload = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        System.out.println("Отримано Gumroad webhook: " + payload);

        // Парсимо x-www-form-urlencoded
        Map<String, String> data = Arrays.stream(payload.split("&"))
                .map(s -> s.split("=", 2))
                .collect(Collectors.toMap(
                        a -> URLDecoder.decode(a[0], StandardCharsets.UTF_8),
                        a -> a.length > 1 ? URLDecoder.decode(a[1], StandardCharsets.UTF_8) : ""
                ));

        // Витягуємо user_id та bot_id
        String userIdStr = data.get("url_params[user_id]");
        String botIdStr = data.get("url_params[bot_id]");
        String resourceName = data.get("resource_name"); // наприклад, "sale"

        System.out.println("Parsed IDs: user_id=" + userIdStr + ", bot_id=" + botIdStr);

        if (userIdStr == null || botIdStr == null) {
            return ResponseEntity.ok("missing ids");
        }

        Long userId = Long.parseLong(userIdStr);
        Long botId = Long.parseLong(botIdStr);

        PlatformUser platformUser = platformUserRepository.findById(userId).orElse(null);
        if (platformUser == null) {
            return ResponseEntity.ok("user not found");
        }

        // Залежно від події робимо різні повідомлення
        String messageText = null;

        switch (resourceName) {
            case "sale":
                messageText = "✅ Дякуємо за оплату! Доступ оновлено.";
                obrobka.sale(userIdStr, botIdStr, resourceName);
                break;
            case "refund":
                messageText = "❌ Ваш платіж було повернено. Доступ анульовано.";
                // TODO: вимкнути підписку
                break;
            case "dispute":
                messageText = "⚠️ По вашій оплаті відкрито спір. Ми зв’яжемось з вами.";
                break;
            case "dispute_won":
                messageText = "🎉 Спір вирішено на нашу користь. Доступ збережено.";
                break;
            case "cancellation":
                messageText = "📌 Ваша підписка скасована. Доступ діятиме до кінця оплаченого періоду.";
                break;
            case "subscription_updated":
                messageText = "🔄 Ваша підписка оновлена.";
                break;
            case "subscription_ended":
                messageText = "🚪 Ваша підписка завершена.";
                break;
            case "subscription_restarted":
                messageText = "🔔 Ваша підписка відновлена!";
                break;
            default:
                System.out.println("Невідома подія: " + resourceName);
        }
        /*
        if (messageText != null) {
            SendMessage msg = new SendMessage();
            msg.setChatId(String.valueOf(platformUser.getTelegramId()));
            msg.setText(messageText);

            try {
                bot.execute(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

         */

        return ResponseEntity.ok("ok");
    }

}
