package com.example.TaxiTelegramBot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PhotoPathConfig {

    @Value("${photo.path}")
    private String photoPath;

    public String getPhotoPath(){
        return photoPath;
    }
}
