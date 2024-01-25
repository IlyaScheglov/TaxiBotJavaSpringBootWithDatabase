package com.example.TaxiTelegramBot.repos;

import com.example.TaxiTelegramBot.entities.Colors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorsRepository extends JpaRepository<Colors, Long> {

    Colors findByTitle(String title);

    @Query("SELECT c FROM Colors c LEFT JOIN FETCH c.drivers WHERE c.id = :cId")
    Colors findColorWithDriversById(@Param("cId") long colorId);
}
