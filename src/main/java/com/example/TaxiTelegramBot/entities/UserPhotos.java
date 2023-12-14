package com.example.TaxiTelegramBot.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "user_photos")
public class UserPhotos implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "way_to_file")
    private String wayToFile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;
}
