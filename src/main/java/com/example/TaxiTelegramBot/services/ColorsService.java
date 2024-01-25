package com.example.TaxiTelegramBot.services;

import com.example.TaxiTelegramBot.entities.Colors;
import com.example.TaxiTelegramBot.entities.Drivers;
import com.example.TaxiTelegramBot.repos.ColorsRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ColorsService {

    private final ColorsRepository colorsRepository;

    public Colors getAndAddIfExcists(String colorTitle){
        Colors color = colorsRepository.findByTitle(colorTitle);
        if(color == null){
            Colors newColor = new Colors();
            newColor.setTitle(colorTitle);
            colorsRepository.save(newColor);
            return newColor;
        }
        return color;
    }

    public void addDriversToColor(Drivers driver, Colors color){
        Colors colorWithDrivers = colorsRepository.findColorWithDriversById(color.getId());
        colorWithDrivers.getDrivers().add(driver);
        colorsRepository.save(colorWithDrivers);
    }
}
