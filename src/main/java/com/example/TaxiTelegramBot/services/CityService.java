package com.example.TaxiTelegramBot.services;

import com.example.TaxiTelegramBot.entities.Cities;
import com.example.TaxiTelegramBot.entities.Drivers;
import com.example.TaxiTelegramBot.entities.Users;
import com.example.TaxiTelegramBot.repos.CityRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CityService {

    private final CityRepository cityRepository;

    public Cities addCityIfItExists(String cityTitle){
        Cities city = cityRepository.findByTitle(cityTitle);
        if(city != null){
            return city;
        }
        else{
            city = new Cities();
            city.setTitle(cityTitle);
            cityRepository.save(city);
            return city;
        }
    }


    public void addUserToCity(Users user, Cities city){
        Cities cityWithUsers = cityRepository.findCityWithUsersById(city.getId());
        cityWithUsers.getUsers().add(user);
        cityRepository.save(cityWithUsers);
    }

    public void addDriverToCity(Drivers driver, Cities city){
        Cities cityWithDrivers = cityRepository.findCityWithDriversById(driver.getId());
        cityWithDrivers.getDrivers().add(driver);
        cityRepository.save(cityWithDrivers);
    }



}
