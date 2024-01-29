package com.example.TaxiTelegramBot.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Data
@Table(name = "rides")
public class Rides implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "time_start")
    private String timeStart;

    @Column(name = "time_finish")
    private String timeFinish;

    @Column(name = "place_start")
    private String placeStart;

    @Column(name = "place_finish")
    private String placeFinish;

    @Column(name = "cost")
    private String cost;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    private Drivers driver;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "class_id", referencedColumnName = "id")
    private AutoClasses autoClass;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private Statuses status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id", referencedColumnName = "id")
    private Cities city;
}
