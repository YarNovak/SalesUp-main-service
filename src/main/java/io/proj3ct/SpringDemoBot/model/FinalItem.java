package io.proj3ct.SpringDemoBot.model;


import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Table(name = "final_items")
public class FinalItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Long cena;
    private int quantity;

    private Long vid;
    private String menu;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bot_id")
    private Bot bot;

}