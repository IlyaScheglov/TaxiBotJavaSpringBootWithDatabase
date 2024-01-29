package com.example.TaxiTelegramBot.repos;

import com.example.TaxiTelegramBot.entities.Rides;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RidesRepository extends JpaRepository<Rides, Long> {
}
