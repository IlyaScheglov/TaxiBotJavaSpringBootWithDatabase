package com.example.TaxiTelegramBot.services;

import com.example.TaxiTelegramBot.entities.Colors;
import com.example.TaxiTelegramBot.repos.ColorsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
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
}
