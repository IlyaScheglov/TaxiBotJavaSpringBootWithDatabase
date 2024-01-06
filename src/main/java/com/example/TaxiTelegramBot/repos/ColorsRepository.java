package com.example.TaxiTelegramBot.repos;

import com.example.TaxiTelegramBot.entities.Colors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorsRepository extends JpaRepository<Colors, Long> {

    Colors findByTitle(String title);
}
