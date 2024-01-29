package com.example.TaxiTelegramBot.services;

import com.example.TaxiTelegramBot.repos.RidesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RidesService {

    private final RidesRepository ridesRepository;


}
