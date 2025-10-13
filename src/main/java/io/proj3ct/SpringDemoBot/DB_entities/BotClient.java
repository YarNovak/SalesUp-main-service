package io.proj3ct.SpringDemoBot.DB_entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bot_clients", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"bot_id", "chat_id"})
})
@Setter
@Getter

public class BotClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bot_id")
    private Bot bot;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "first_interaction")
    private LocalDateTime firstInteraction = LocalDateTime.now();

    @Column(name = "last_message")
    private String lastMessage;

}
