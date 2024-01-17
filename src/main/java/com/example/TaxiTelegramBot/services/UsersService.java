package com.example.TaxiTelegramBot.services;

import com.example.TaxiTelegramBot.entities.Users;
import com.example.TaxiTelegramBot.repos.UsersRepository;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import lombok.RequiredArgsConstructor;
import org.glassfish.jersey.server.monitoring.ExceptionMapperStatistics;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    private final CityService cityService;


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

    public String getUserMoney(long chatId){
        Users user = findUserByChatId(chatId);
        return user.getMoney();
    }

    public String addMoneyToBalance(long chatId, String howMuch) {
        Users user = findUserByChatId(chatId);
        BigDecimal userMoney = new BigDecimal(user.getMoney());
        BigDecimal addingMoney = null;
        try {
            addingMoney = new BigDecimal(howMuch);
        } catch (Exception e) {
            return "Вы ввели что-то неверно";
        }
        user.setMoney(String.valueOf(userMoney.add(addingMoney)));
        usersRepository.save(user);
        return "Вы успешно пополнили баланс";
    }

    public void registerNewUser(Users user){
        user.setMoney("0.00");
        usersRepository.save(user);
        cityService.addUserToCity(user, user.getCity());
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
