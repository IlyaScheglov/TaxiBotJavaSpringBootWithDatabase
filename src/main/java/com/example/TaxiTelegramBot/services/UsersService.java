package com.example.TaxiTelegramBot.services;

import com.example.TaxiTelegramBot.entities.Users;
import com.example.TaxiTelegramBot.repos.UsersRepository;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import lombok.RequiredArgsConstructor;
import org.jvnet.hk2.annotations.Service;
import org.springframework.context.annotation.Bean;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersService {

    @Bean
    public Argon2 argon2(){
        return Argon2Factory.create();
    }

    private final UsersRepository usersRepository;

    public boolean chechUserLoginOrNot(long chatId){
        List<Users> users = usersRepository.findByChatId(chatId);
        return !users.isEmpty();
    }

    public String loginUser(long chatId, String login, String password){
        Users user = usersRepository.findByLogin(login);
        if(!checkCanWeAutorizeToThisLogin(login)){
            return "Вы не можете авторизоваться по такому логину!";
        }
        else if(!argon2().verify(user.getPassword(), password)){
            return "Введен неверный пароль!";
        }
        else{
            user.setChatId(chatId);
            return "Вы успешно авторизовались!";
        }
    }

    private boolean checkCanWeAutorizeToThisLogin(String login){
        Users user = usersRepository.findByLogin(login);
        if(user == null){
            return false;
        }
        else{
            return user.getChatId() == 0;
        }
    }
}
