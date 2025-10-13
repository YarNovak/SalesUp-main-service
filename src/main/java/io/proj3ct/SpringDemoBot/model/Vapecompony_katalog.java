package io.proj3ct.SpringDemoBot.model;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "vapecompony_katalog")
@Setter
@Getter
public class Vapecompony_katalog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long kilkist;
    private Long cena;

    @Column(nullable = false)
    private String name;


    @Lob
    @Column(nullable = true)
    private byte[] photo;

    private String photoMimeType;

    @Lob
    @Column(nullable = true)
    private byte[] video;

    private String videoMimeType;


    @Lob
    @Column(name = "entities_json", columnDefinition = "TEXT")
    private String entitiesJson;

    @Lob
    @Column(name = "entities_json_desc", columnDefinition = "TEXT")
    private String entitiesDescJson;


    private String description;

    @ManyToOne
    @JoinColumn(name = "vapecompony_id")
    private Vapecompony vapecompony;

    public Vapecompony_katalog() {}
    public Vapecompony_katalog(String name, Long kilkist, Long cena) {
        this.name = name;
        this.kilkist = kilkist;
        this.cena = cena;
    }

    public Vapecompony_katalog(String name) {
        this.name = name;

    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bot_id")
    private Bot bot;



}