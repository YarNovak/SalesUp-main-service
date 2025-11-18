package io.proj3ct.SpringDemoBot.DaO.BotCreating.Handlers.LastShtrifBotFather;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.PlatformUser;
import io.proj3ct.SpringDemoBot.DaO.BotCreating.BotDefaultValues;
import io.proj3ct.SpringDemoBot.DaO.BotCreating.Handlers.Wait_BotFather;
import io.proj3ct.SpringDemoBot.DaO.BotCreating.TgTokenvalidator;
import io.proj3ct.SpringDemoBot.DaO.MessageHandle;

import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.model.UserRepository;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.repository.PlatformUserRepository;
import io.proj3ct.SpringDemoBot.service.WebhookService;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class BotFatherLastShtrih implements MessageHandle {

    @Autowired
    private Wait_BotFather waitBotFather;

    @Autowired
    private WebhookService webhookService;

    @Autowired
    private TgTokenvalidator tgTokenvalidator;

    @Autowired
    private BotRepository botRepository;

    @Autowired
    private PlatformUserRepository userRepository;
    @Autowired
    private BotDefaultValues defaultValues;

    @Autowired
    private MessageRegistry messageRegistry;

    @Override
    public boolean support(Message msgcallbackData) {
        return waitBotFather.iswaiting_foruser(msgcallbackData.getFrom().getId());
    }

    @Override
    public void handle(Message msg, TelegramLongPollingBot bot) {

        SendMessage message = new SendMessage();
        message.setChatId(msg.getChatId().toString());

            if(TgTokenvalidator.isValidTelegramToken(msg.getText())){

                Long userId = msg.getFrom().getId();

//                TODO: Issue with ID and connecting texts from def. values with bot_message and checking of prior existence of given token

                Optional<PlatformUser> userOpt = userRepository.findByTelegramId(userId);
                Bot botik = new Bot();
                botik .setOwner(userOpt.get());
                botik .setSubscriptionStatus("free");
                botik .setCurrentPrice(BigDecimal.ZERO);
                botik .setRegistrationDate(LocalDateTime.now());
                botik .setPaymentDue(LocalDateTime.now().plusDays(7));
                botik.setActive(true);
                botik.setBotToken(msg.getText());
                botik.setBotusername(TgTokenvalidator.printBotInfo(msg.getText()).getUserName());
                botik.setName(TgTokenvalidator.printBotInfo(msg.getText()).getFirstName());

                botik.setCart(true);
                botik.setNalichka(true);

              //  defaultValues.setDefault(botik);
                botik .create();


               // defaultValues.setDefault(botik);
                System.out.println("Creating bot with token from user: " + userId);
                botRepository.save(botik);
               // defaultValues.setDefault(botik);
                System.out.println("Bot saved to repository with ID: " + botik.getId());

                // Register webhook for the new bot
                webhookService.registerTenantWebhook(botik.getBotToken());
                System.out.println("Webhook registered for bot ID: " + botik.getId());


                waitBotFather.clear(msg.getFrom().getId());

                String botUsername = extractUsernameFromToken(botik.getBotToken());
                String link = "https://t.me/" + botUsername;

                String text =
                        "🎉 Ваш бот успешно создан!\n\n" +
                                "Запустите его тут:\n" + link + "\n\n" +
                                "🔓 Пробный период: 7 дней.";

                InlineKeyboardButton goToBotButton = new InlineKeyboardButton("🚀 Открыть бота");
                goToBotButton.setUrl(link);

                InlineKeyboardButton editFunctionalityButton = new InlineKeyboardButton("⚙ Изменить функционал бота");
                editFunctionalityButton.setCallbackData("EDIT_BOT_FUNCTION:" + botik.getBotToken());

                List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                keyboard.add(Collections.singletonList(goToBotButton));
                keyboard.add(Collections.singletonList(editFunctionalityButton));

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                markup.setKeyboard(keyboard);

                message.setText(text);
                message.setReplyMarkup(markup);



            }
            else{

                message.setText("Ваш токен або неправильний,а бо ж не дыйсний, надышлыть новий))");

            }
            try{
                Message msgg =  bot.execute(message);
                messageRegistry.addMessage(msgg.getChatId(), msgg.getMessageId());

            }
            catch (TelegramApiException e){
                e.printStackTrace();
            }

    }
    private String extractUsernameFromToken(String token) {

        Bot need_bot = botRepository.findByBotToken(token).get();
        return need_bot.getBotusername();
    }





}
