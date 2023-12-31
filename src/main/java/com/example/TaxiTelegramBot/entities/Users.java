package com.example.TaxiTelegramBot.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class Users implements Serializable {

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

    @Column(name = "money")
    private String money;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<Rides> rides = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_if", referencedColumnName = "id")
    private Cities city;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<UserPhotos> userPhotos = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<Reviews> reviews = new ArrayList<>();
}
