package com.example.TaxiTelegramBot.services;

import com.example.TaxiTelegramBot.entities.DriverPhotos;
import com.example.TaxiTelegramBot.repos.DriverPhotosRepository;
import com.example.TaxiTelegramBot.repos.DriversRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverPhotosService {

    private final DriverPhotosRepository driverPhotosRepository;

    public DriverPhotos addNewPhoto(String path){
        DriverPhotos photo = new DriverPhotos();
        photo.setWayToFile(path);
        driverPhotosRepository.save(photo);
        return photo;
    }
}
