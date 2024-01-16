package com.example.TaxiTelegramBot.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "classes")
public class AutoClasses implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "title")
    private String title;

    @Column(name = "cost_for_km")
    private String costForKm;

    @Column(name = "standard_cost")
    private String standardCost;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "autoClass")
    private List<Rides> rides = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "autoClass")
    private List<Drivers> drivers = new ArrayList<>();
}
