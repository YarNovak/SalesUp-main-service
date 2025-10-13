package io.proj3ct.SpringDemoBot.model;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Table(name = "cart_items")

public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long chatId;
    private Long temporary;

    @ManyToOne
    @JoinColumn(name = "vapecomponyKatalog_id")
    private Vapecompony_katalog vapecomponyKatalog;




    private int quantity = 1;

    public CartItem() {}

    public CartItem(Long chatId, Vapecompony_katalog product) {
        this.chatId = chatId;
        this.vapecomponyKatalog = product;
    }

    @ManyToOne
    @JoinColumn(name = "orders_id")
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bot_id")
    private Bot bot;
}
