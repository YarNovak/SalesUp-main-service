package io.proj3ct.SpringDemoBot.DB_entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bot_settings")
@Setter
@Getter

public class BotSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "bot_id")
    private Bot bot;

    @Column(name = "settings_json", columnDefinition = "jsonb")
    private String settingsJson; // Можеш використати JsonNode для парсингу через Jackson



}
