package io.proj3ct.SpringDemoBot.model;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "Orders")

public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatId;
    private String cash_card;
    private boolean paid;
    private String delivery;
    private String customer_money;

    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Timestamp createdAt;
    private String currency;

    //private String type;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FinalItem> finalItems = new ArrayList<>();

    public Orders() {}

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bot_id")
    private Bot bot;

}
