package io.proj3ct.SpringDemoBot.DaO.PayForSalesUp.PaymentDuties;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import io.proj3ct.SpringDemoBot.DB_entities.PlatformUser;
import io.proj3ct.SpringDemoBot.HelpingServise.CleanBot.CleanTheBot;
import io.proj3ct.SpringDemoBot.HelpingServise.EditDelete_Messages.MessageRegistry;
import io.proj3ct.SpringDemoBot.repository.BotRepository;
import io.proj3ct.SpringDemoBot.repository.PlatformUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;

@Service
public class Obrobka {

    @Autowired
    private PlatformUserRepository platformUserRepository;
    @Autowired
    private BotRepository botRepository;
    @Autowired
    private TelegramLongPollingBot bot;

    @Autowired
    private CleanTheBot cleanTheBot;
    @Autowired
    private MessageRegistry messageRegistry;


    public void sale( String userIdStr, String botIdStr,  String resourceName){

        Long userId;
        Long botId;

        PlatformUser platformUser;
        Bot botEntity;

        if(userIdStr != null && botIdStr != null) {
          userId = Long.parseLong(userIdStr);
          botId = Long.parseLong(botIdStr);

            platformUser = platformUserRepository.findById(userId).orElse(null);
            botEntity = botRepository.findById(botId).orElse(null); // потрібен BotRepository

        }
        else return;

        if ("sale".equals(resourceName)) {

            if (platformUser != null && botEntity != null) {

                // ====== ЛОГІКА ПІДПИСКИ ======
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime baseDate = botEntity.getPaymentDue() != null && botEntity.getPaymentDue().isAfter(now)
                        ? botEntity.getPaymentDue()
                        : now;

                // +1 місяць до підписки
                LocalDateTime newDue = baseDate.plusMonths(1);
                botEntity.setPaymentDue(newDue);
                botEntity.setSubscriptionStatus("ACTIVE");
                botRepository.save(botEntity);

                // ====== ПОВІДОМЛЕННЯ КЛІЄНТУ ======
                SendMessage msg = new SendMessage();
                msg.setChatId(String.valueOf(platformUser.getTelegramId()));
                msg.setText("✅ Оплата прошла успешно!\n\n Доступ к вашему боту продлён до " + newDue.toLocalDate()+"\nSalesUp помогает вашему бизнесу расти\uD83D\uDE80");

                try {

                    Message thisMess = bot.execute(msg);
                    messageRegistry.addMessage(thisMess.getChatId(), thisMess.getMessageId());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
