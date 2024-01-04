package com.example.TaxiTelegramBot.repos;

import com.example.TaxiTelegramBot.entities.Drivers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriversRepository extends JpaRepository<Drivers, Long> {

    Drivers findByChatId(long chatId);
}
