package com.example.TaxiTelegramBot.services;

import com.example.TaxiTelegramBot.entities.Drivers;
import com.example.TaxiTelegramBot.entities.Marks;
import com.example.TaxiTelegramBot.repos.MarksRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MarksService {

    private final MarksRepository marksRepository;

    public Marks getAndAddIfExcists(String markTitle){
        Marks mark = marksRepository.findByTitle(markTitle);
        if(mark == null){
            Marks newMark = new Marks();
            newMark.setTitle(markTitle);
            marksRepository.save(newMark);
            return newMark;
        }
        return mark;
    }

    public void addDriverToMark(Drivers driver, Marks mark){
        mark.getDrivers().add(driver);
        marksRepository.save(mark);
    }
}
