package io.proj3ct.SpringDemoBot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Data
@PropertySource("application.properties")
public class BotConfig {

    Integer count = 0;

    @Value("${bot.name}")
    String botName;

    @Value("${bot.token}")
    String token;

    @Value("${bot.owner}")
    Long ownerId;
    @Value("asdasdad")
    String PROVIDER_TOKEN;

    @Value("${bot.ukrcard}")
    String ukr_card;

     @Value("${bot.plncards}")
     String pln_cards;

    @Value("${bot.plncardr}")
    String pln_cardr;

    @Value("${bot.plncardv}")
    String pln_cardv;



    public String get_exet_card(){

        if(count == 3) count=0;
        count++;
        switch (count.toString()) {
            case "1":
                System.out.println(pln_cards);
                return pln_cards;

            case "2":
                System.out.println(pln_cardr);
                return pln_cardr;
            default:
                System.out.println(pln_cardv);
                return pln_cardv;

        }

    }









}
