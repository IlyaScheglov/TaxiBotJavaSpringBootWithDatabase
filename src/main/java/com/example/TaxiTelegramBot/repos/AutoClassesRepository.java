package com.example.TaxiTelegramBot.repos;

import com.example.TaxiTelegramBot.entities.AutoClasses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoClassesRepository extends JpaRepository<AutoClasses, Long> {

    AutoClasses findByTitle(String title);
}
