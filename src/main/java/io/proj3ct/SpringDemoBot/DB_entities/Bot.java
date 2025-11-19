package io.proj3ct.SpringDemoBot.DB_entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import io.hypersistence.utils.hibernate.id.Tsid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "bots")
@Setter
@Getter
public class Bot {
    @Id
    @Tsid
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private PlatformUser owner;

    private String name;
    private String botusername;

    @Column(name = "bot_token", nullable = false)
    private String botToken;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Column(name = "subscription_status")
    private String subscriptionStatus;

    @Column(name = "current_price")
    private BigDecimal currentPrice = BigDecimal.ZERO;

    @Column(name = "payment_due")
    private LocalDateTime paymentDue;

    @OneToMany(mappedBy = "bot", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BotClient> clients = new ArrayList<>();


    @OneToOne(mappedBy = "bot", cascade = CascadeType.ALL, orphanRemoval = true)
    private BotSettings settings;

    @OneToMany(mappedBy = "bot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiscountProgress> discounts = new ArrayList<>();

    @Column(name = "active", nullable = false)
    private boolean active;


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bot_button_texts", joinColumns = @JoinColumn(name = "bot_id"))
    @MapKeyColumn(name = "button_key")
    @Column(name = "button_text")
    private Map<String, String> buttonTexts = new HashMap<>();

    private boolean Nalichka;
    private boolean Cart;

    public void create() {
        //   buttonTexts.put("catalog", "🛍 Каталог");
        //  buttonTexts.put("payment", "💳 Оплата");
        //   buttonTexts.put("cart", "🛒 Корзина");
        //   buttonTexts.put("help", "❓ Допомога");

        buttonTexts.put("info", "Інформація");
        buttonTexts.put("change", "✏\uFE0FИзменить");
        buttonTexts.put("add_first", "бомбический выбор");
        buttonTexts.put("first_add", "добавить");
        buttonTexts.put("empty_add", "❌Нету в наличии");
        buttonTexts.put("add", "+");
        buttonTexts.put("clear", "Очистить");
        buttonTexts.put("payment", "\uD83D\uDCB8Оплата");
        buttonTexts.put("cash_method", "Cash");
        buttonTexts.put("cart_method", "Card");
        buttonTexts.put("delete", "-");
        buttonTexts.put("catalog", "🛍Каталог");
        buttonTexts.put("order", "Замовлення");
        buttonTexts.put("contact", "Контакти");
        buttonTexts.put("cart", "\uD83D\uDED2Корзина");
        buttonTexts.put("curr", "zł");

    }


    @OneToMany(mappedBy = "bot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BotMessage> botmessages = new ArrayList<>();


}
