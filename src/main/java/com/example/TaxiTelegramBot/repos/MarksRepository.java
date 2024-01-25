package com.example.TaxiTelegramBot.repos;

import com.example.TaxiTelegramBot.entities.Marks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MarksRepository extends JpaRepository<Marks, Long> {

    Marks findByTitle(String title);

    @Query("SELECT m FROM Marks m LEFT JOIN FETCH m.drivers WHERE m.id = :mId")
    Marks findMarkWithDriversById(@Param("mId") long markId);
}
