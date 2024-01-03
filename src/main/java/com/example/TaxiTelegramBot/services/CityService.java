package com.example.TaxiTelegramBot.services;

import com.example.TaxiTelegramBot.entities.Cities;
import com.example.TaxiTelegramBot.entities.Users;
import com.example.TaxiTelegramBot.repos.CityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
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
        List<Users> users = city.getUsers();
        users.add(user);
        city.setUsers(users);
        cityRepository.save(city);
    }
}
