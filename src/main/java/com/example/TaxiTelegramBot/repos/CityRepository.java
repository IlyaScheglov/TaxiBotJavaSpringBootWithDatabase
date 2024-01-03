package com.example.TaxiTelegramBot.repos;

import com.example.TaxiTelegramBot.entities.Cities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<Cities, Long> {

    Cities findByTitle(String title);
}
