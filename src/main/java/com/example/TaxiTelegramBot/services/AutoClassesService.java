package com.example.TaxiTelegramBot.services;

import com.example.TaxiTelegramBot.entities.AutoClasses;
import com.example.TaxiTelegramBot.entities.Drivers;
import com.example.TaxiTelegramBot.repos.AutoClassesRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AutoClassesService {

    private final AutoClassesRepository autoClassesRepository;

    public List<String> findAllClassesTitle(){
        List<AutoClasses> classes = findAll();
        List<String> result = classes.stream()
                .map(c -> c.getTitle()).collect(Collectors.toList());
        return result;
    }

    public List<AutoClasses> findAll(){
        return autoClassesRepository.findAll();
    }

    public AutoClasses getAutoClassByTitle(String title){
        return autoClassesRepository.findByTitle(title);
    }

    public void addDriverToClasses(Drivers driver, AutoClasses autoClass){
        AutoClasses autoClassWithDrivers = autoClassesRepository
                .findAutoClassWithDriversById(autoClass.getId());
        autoClassWithDrivers.getDrivers().add(driver);
        autoClassesRepository.save(autoClassWithDrivers);
    }
}
