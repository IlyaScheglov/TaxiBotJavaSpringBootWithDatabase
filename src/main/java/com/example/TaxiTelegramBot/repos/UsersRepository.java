package com.example.TaxiTelegramBot.repos;

import com.example.TaxiTelegramBot.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    List<Users> findByChatId(long chatId);

    Users findByLogin(String login);
}
