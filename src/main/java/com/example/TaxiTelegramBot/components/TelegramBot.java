package com.example.TaxiTelegramBot.components;


import com.example.TaxiTelegramBot.entities.Cities;
import com.example.TaxiTelegramBot.entities.TypeOfSpecialMessage;
import com.example.TaxiTelegramBot.entities.Users;
import com.example.TaxiTelegramBot.services.CityService;
import com.example.TaxiTelegramBot.services.UsersService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private Map<Long, TypeOfSpecialMessage> specialMessagesMap = new HashMap<>();

    private Users newUserToRegister = new Users();

    private final UsersService usersService;

    private final CityService cityService;

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String token;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken(){
        return token;
    }
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            if(specialMessagesMap.containsKey(update.getMessage().getChatId())){
                specialMessagesHandler(update.getMessage(),
                        specialMessagesMap.get(update.getMessage().getChatId()));
            }
            else {
                Message message = update.getMessage();
                String text = message.getText();
                switch (text) {
                    case "/start":
                        startMessage(message);
                        break;

                    case "/help":
                        helpMessage(message);
                        break;

                    case "/register_as_user":
                        startRegistration(message);
                        break;

                    default:
                        defaultMessage(message);
                        break;
                }
            }
        }
    }

    private void startMessage(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        messageToSend.setText("Привет, это телеграм бот для заказа такси и работы в этой области!\n" +
                "Для просмотра полного перечня команд введите /help");

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void helpMessage(Message message){
        String helpMess = """
                /start - команда для старта работы с ботом
                /help - команда для просмотра возможностей бота
                /login_as_user - команда для входа в аккаунт пользователя
                /login_as_driver - команда для входа в аккаунт водителя
                /logout_as_user - команда для выхода из аккаунта пользователя
                /logout_as_driver - команда для выхода из аккаунта водителя
                /register_as_user - команда для регистрации нового пользователя
                /register_as_driver - команда для регистрации нового водителя
                """;

        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        messageToSend.setText(helpMess);

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void defaultMessage(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        messageToSend.setText("Такой команды нет в списке");

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void startRegistration(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());

        if(usersService.checkUserLoginOrNot(message.getChatId())){
            messageToSend.setText("Вы уже вошли в аккаунт");
        }
        else{
            messageToSend.setText("Начинаем регистрацию, введите логин");
            specialMessagesMap.put(message.getChatId(),
                    TypeOfSpecialMessage.USER_REGISTER_LOGIN);
            newUserToRegister = new Users();
        }

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }


    private void specialMessagesHandler(Message message, TypeOfSpecialMessage type){
        if(type.equals(TypeOfSpecialMessage.USER_REGISTER_LOGIN)){
            userRegisterLogin(message);
        }
        else if(type.equals(TypeOfSpecialMessage.USER_REGISTER_PASSWORD)){
            userRegisterPassword(message);
        }
        else if(type.equals(TypeOfSpecialMessage.USER_REGISTER_FIO)){
            userRegisterFIO(message);
        }
        else if(type.equals(TypeOfSpecialMessage.USER_REGISTER_CITY)){
            userRegisterCity(message);
        }
    }

    private void userRegisterLogin(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        String login = message.getText();
        if(usersService.checkCanWeRegisterThisUser(login)){
            messageToSend.setText("Введите пароль");
            specialMessagesMap.put(message.getChatId(),
                    TypeOfSpecialMessage.USER_REGISTER_PASSWORD);
            newUserToRegister.setLogin(login);
        }
        else{
            messageToSend.setText("Пользователь с таким логином уже существует, попробуйте другой");
        }

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void userRegisterPassword(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        String password = message.getText();
        messageToSend.setText("Введите ФИО");
        specialMessagesMap.put(message.getChatId(),
                TypeOfSpecialMessage.USER_REGISTER_FIO);
        newUserToRegister.setPassword(usersService.hashPassword(password));

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void userRegisterFIO(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        String fio = message.getText();
        messageToSend.setText("Введите название своего города");
        specialMessagesMap.put(message.getChatId(),
                TypeOfSpecialMessage.USER_REGISTER_CITY);
        newUserToRegister.setFio(fio);

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void userRegisterCity(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        String city = message.getText().toUpperCase();
        messageToSend.setText("Вы успешно зарегестрировались");
        specialMessagesMap.remove(message.getChatId());
        Cities realCity = cityService.addCityIfItExists(city);
        newUserToRegister.setCity(realCity);
        newUserToRegister.setChatId(message.getChatId());
        Users user = usersService.registerNewUser(newUserToRegister);
        cityService.addUserToCity(user, user.getCity());

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

}
