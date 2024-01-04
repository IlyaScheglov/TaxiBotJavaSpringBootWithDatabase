package com.example.TaxiTelegramBot.services;

import com.example.TaxiTelegramBot.entities.Drivers;
import com.example.TaxiTelegramBot.repos.DriversRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriversService {

    private final DriversRepository driversRepository;

    public boolean checkDriverLoginOrNot(long chatId){
        Drivers driver = getDriverByChatId(chatId);
        return driver != null;
    }

    private Drivers getDriverByChatId(long chatId){
        return driversRepository.findByChatId(chatId);
    }
}
