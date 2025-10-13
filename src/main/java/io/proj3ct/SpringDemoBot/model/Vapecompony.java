package io.proj3ct.SpringDemoBot.model;

import io.proj3ct.SpringDemoBot.DB_entities.Bot;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Vapecompony")
@Getter
@Setter
public class Vapecompony {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;


    private Long messageLink;
    private String description;

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

    public Vapecompony() {}

    @OneToMany(mappedBy = "vapecompony", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vapecompony_katalog> submenuItems = new ArrayList<>();

    public Vapecompony(String name, Long messageLink) {
        this.name = name;
        this.messageLink = messageLink;

    }
    public Vapecompony(String name) {
        this.name = name;

    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bot_id")
    private Bot bot;

}
