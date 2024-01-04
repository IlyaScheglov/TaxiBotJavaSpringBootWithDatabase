package com.example.TaxiTelegramBot.services;

import com.example.TaxiTelegramBot.entities.Users;
import com.example.TaxiTelegramBot.repos.UsersRepository;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;


    public boolean checkUserLoginOrNot(long chatId){
        Users user = findUserByChatId(chatId);
        return user != null;
    }

    public String loginUser(long chatId, String login, String password){
        Users user = usersRepository.findByLogin(login);
        if(!checkCanWeAutorizeToThisLogin(login)){
            return "Вы не можете авторизоваться по такому логину";
        }
        else if(!getArgon().verify(user.getPassword(), password)){
            return "Введен неверный пароль";
        }
        else{
            user.setChatId(chatId);
            usersRepository.save(user);
            return "Вы успешно авторизовались";
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


    public Users registerNewUser(Users user){
        usersRepository.save(user);
        return user;
    }

    public boolean checkCanWeRegisterThisUser(String login){
        Users user = usersRepository.findByLogin(login);
        return user == null;
    }

    public void logout(long chatId){
        Users user = findUserByChatId(chatId);
        user.setChatId(0);
        usersRepository.save(user);
    }

    public String hashPassword(String notHashedPassword){
        return getArgon().hash(22, 65536, 1, notHashedPassword);
    }

    private Argon2 getArgon(){
        return Argon2Factory.create();
    }

    private Users findUserByChatId(long chatId){
        return usersRepository.findByChatId(chatId);
    }
}
