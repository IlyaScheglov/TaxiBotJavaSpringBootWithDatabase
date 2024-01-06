package com.example.TaxiTelegramBot.repos;

import com.example.TaxiTelegramBot.entities.Marks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarksRepository extends JpaRepository<Marks, Long> {

    Marks findByTitle(String title);
}
