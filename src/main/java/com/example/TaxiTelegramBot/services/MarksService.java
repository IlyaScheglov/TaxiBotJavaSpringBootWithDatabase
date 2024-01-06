package com.example.TaxiTelegramBot.services;

import com.example.TaxiTelegramBot.entities.Marks;
import com.example.TaxiTelegramBot.repos.MarksRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
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
}
