package io.proj3ct.SpringDemoBot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Menu {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    private String description;

    @ManyToOne
    @JoinColumn(name = "vapecompony_id", nullable = false)
    private Vapecompony vapecompony;



}

