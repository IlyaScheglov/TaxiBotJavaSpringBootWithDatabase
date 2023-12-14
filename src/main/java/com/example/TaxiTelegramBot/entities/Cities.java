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
@Table(name = "cities")
public class Cities implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "title")
    private String title;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "city")
    private List<Rides> rides = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "city")
    private List<Users> users = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "city")
    private List<Drivers> drivers = new ArrayList<>();
}
