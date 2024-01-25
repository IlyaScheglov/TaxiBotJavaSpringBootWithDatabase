package com.example.TaxiTelegramBot.repos;

import com.example.TaxiTelegramBot.entities.AutoClasses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoClassesRepository extends JpaRepository<AutoClasses, Long> {

    AutoClasses findByTitle(String title);

    @Query("SELECT ac FROM AutoClasses ac LEFT JOIN FETCH ac.drivers WHERE ac.id = :acId")
    AutoClasses findAutoClassWithDriversById(@Param("acId") long autoClassId);
}
