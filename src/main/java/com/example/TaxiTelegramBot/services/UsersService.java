package com.example.TaxiTelegramBot.services;

import com.example.TaxiTelegramBot.entities.Users;
import com.example.TaxiTelegramBot.repos.UsersRepository;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import lombok.RequiredArgsConstructor;
import org.jvnet.hk2.annotations.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;


    public boolean checkUserLoginOrNot(long chatId){
        List<Users> users = usersRepository.findByChatId(chatId);
        return !users.isEmpty();
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

    public String hashPassword(String notHashedPassword){
        return getArgon().hash(22, 65536, 1, notHashedPassword);
    }

    private Argon2 getArgon(){
        return Argon2Factory.create();
    }
}
