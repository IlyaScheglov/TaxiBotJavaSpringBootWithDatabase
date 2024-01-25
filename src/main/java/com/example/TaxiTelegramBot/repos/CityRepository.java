package com.example.TaxiTelegramBot.repos;

import com.example.TaxiTelegramBot.entities.Cities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<Cities, Long> {

    Cities findByTitle(String title);

    @Query("SELECT c FROM Cities c LEFT JOIN FETCH c.users WHERE c.id = :cId")
    Cities findCityWithUsersById(@Param("cId") long cityId);

    @Query("SELECT c FROM Cities c LEFT JOIN FETCH c.drivers WHERE c.id = :cId")
    Cities findCityWithDriversById(@Param("cId") long cityId);
}
