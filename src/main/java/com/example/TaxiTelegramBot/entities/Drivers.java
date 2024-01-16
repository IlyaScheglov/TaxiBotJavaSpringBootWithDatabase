package com.example.TaxiTelegramBot.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "drivers")
public class Drivers implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "fio")
    private String fio;

    @Column(name = "auto_number")
    private String autoNumber;

    @Column(name = "drive_expirience")
    private int driveExpirience;

    @Column(name = "money")
    private String money;

    @Column(name = "active")
    private boolean active;

    @Column(name = "photo_path")
    private String photoPath;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "driver")
    private List<Rides> rides = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "class_id", referencedColumnName = "id")
    private AutoClasses autoClass;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private Cities city;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "auto_mark_id", referencedColumnName = "id")
    private Marks mark;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "auto_color_id", referencedColumnName = "id")
    private Colors color;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "driver")
    private List<Reviews> reviews = new ArrayList<>();
}
