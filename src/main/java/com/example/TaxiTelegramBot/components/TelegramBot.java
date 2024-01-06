package com.example.TaxiTelegramBot.components;


import com.example.TaxiTelegramBot.entities.*;
import com.example.TaxiTelegramBot.enums.TypeOfSpecialMessage;
import com.example.TaxiTelegramBot.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${photo.path}")
    private static String photoPath;

    private Map<Long, TypeOfSpecialMessage> specialMessagesMap = new HashMap<>();

    private Users newUserToRegister = new Users();

    private Drivers newDriverToRegister = new Drivers();

    private final UsersService usersService;

    private final CityService cityService;

    private final DriversService driversService;

    private final AutoClassesService autoClassesService;

    private final MarksService marksService;

    private final ColorsService colorsService;

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

                    case "/login_as_user":
                        startLoginUser(message);
                        break;

                    case "/logout_as_user":
                        logoutAsUser(message);
                        break;

                    case "/register_as_driver":
                        startRegistrationAsDriver(message);
                        break;

                    default:
                        defaultMessage(message);
                        break;
                }
            }
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
        else if(type.equals(TypeOfSpecialMessage.USER_AUTORIZE)){
            userAutorize(message);
        }
        else if(type.equals(TypeOfSpecialMessage.DRIVER_REGISTER_LOGIN)){
            driverRegistrationLogin(message);
        }
        else if(type.equals(TypeOfSpecialMessage.DRIVER_REGISTER_PASSWORD)){
            driverRegistrationPassword(message);
        }
        else if(type.equals(TypeOfSpecialMessage.DRIVER_REGISTER_FIO)){
            driverRegistrationFio(message);
        }
        else if(type.equals(TypeOfSpecialMessage.DRIVER_REGISTER_CITY)){
            driverRegistrationCity(message);
        }
        else if(type.equals(TypeOfSpecialMessage.DRIVER_REGISTER_AUTO_NUMBER)){
            driverRegistrationAutoNumber(message);
        }
        else if(type.equals(TypeOfSpecialMessage.DRIVER_REGISTER_DRIVE_EXPIRIENCE)){
            driverRegistrationDriveExpirience(message);
        }
        else if(type.equals(TypeOfSpecialMessage.DRIVER_REGISTER_AUTO_CLASS)){
            driverRegistrationAutoClass(message);
        }
        else if(type.equals(TypeOfSpecialMessage.DRIVER_REGISTER_AUTO_MARK)){
            driverRegistrationAutoMark(message);
        }
        else if(type.equals(TypeOfSpecialMessage.DRIVER_REGISTER_AUTO_COLOR)){
            driverRegistrationAutoColor(message);
        }
        else if(type.equals(TypeOfSpecialMessage.DRIVER_REGISTER_PHOTO)){
            driverRegistrationPhoto(message);
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

    private void startLoginUser(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());

        if(usersService.checkUserLoginOrNot(message.getChatId())){
            messageToSend.setText("Вы уже вошли в аккаунт");
        }
        else{
            messageToSend.setText("Введите логин и пароль через пробел");
            specialMessagesMap.put(message.getChatId(),
                    TypeOfSpecialMessage.USER_AUTORIZE);
        }

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void userAutorize(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        String[] loginAndPAssword = message.getText().split(" ");
        if(loginAndPAssword.length != 2){
            messageToSend.setText("Вы ввели данные неверно");
        }
        else{
            String answerFromServer = usersService
                    .loginUser(message.getChatId(),
                            loginAndPAssword[0], loginAndPAssword[1]);
            messageToSend.setText(answerFromServer);
        }

        specialMessagesMap.remove(message.getChatId());
        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void logoutAsUser(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        if(!usersService.checkUserLoginOrNot(message.getChatId())){
            messageToSend.setText("Вы еще не вошли в аккаунт");
        }
        else{
            usersService.logout(message.getChatId());
            messageToSend.setText("Вы вышли из аккаунта");
        }

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void startRegistrationAsDriver(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());

        if(driversService.checkDriverLoginOrNot(message.getChatId())){
            messageToSend.setText("Вы уже вошли в аккаунт водителя");
        }
        else{
            messageToSend.setText("Начинаем регистрацию, введите логин");
            specialMessagesMap.put(message.getChatId(),
                    TypeOfSpecialMessage.DRIVER_REGISTER_LOGIN);
            newDriverToRegister = new Drivers();
        }

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void driverRegistrationLogin(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        String login = message.getText();
        if(driversService.checkCanWeRegisterToThisLogin(login)){
            messageToSend.setText("Введите пароль");
            specialMessagesMap.put(message.getChatId(),
                    TypeOfSpecialMessage.DRIVER_REGISTER_PASSWORD);
            newDriverToRegister.setLogin(login);
        }
        else{
            messageToSend.setText("Водитель с таким логином уже существует, попробуйте другой");
        }

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void driverRegistrationPassword(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        messageToSend.setText("Введите ФИО");
        String password = driversService.hashPassword(message.getText());
        specialMessagesMap.put(message.getChatId(),
                TypeOfSpecialMessage.DRIVER_REGISTER_FIO);
        newDriverToRegister.setPassword(password);

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void driverRegistrationFio(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        messageToSend.setText("Введите название своего города");
        specialMessagesMap.put(message.getChatId(),
                TypeOfSpecialMessage.DRIVER_REGISTER_CITY);
        newDriverToRegister.setFio(message.getText());

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void driverRegistrationCity(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        Cities city = cityService.addCityIfItExists(message.getText().toUpperCase());
        messageToSend.setText("Введите номер автомобиля в формате: А111АА12");
        specialMessagesMap.put(message.getChatId(),
                TypeOfSpecialMessage.DRIVER_REGISTER_AUTO_NUMBER);
        newDriverToRegister.setCity(city);

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void driverRegistrationAutoNumber(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        String number = message.getText().toUpperCase();
        if(!driversService.checkValidNumberOrNot(number)){
            messageToSend.setText("Вы ввели номер в неверном формате, посмотрите на сообщение выше");
        }
        else{
            messageToSend.setText("Введите сколько лет вы водите автомобиль");
            specialMessagesMap.put(message.getChatId(),
                    TypeOfSpecialMessage.DRIVER_REGISTER_DRIVE_EXPIRIENCE);
            newDriverToRegister.setAutoNumber(number);
        }

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void driverRegistrationDriveExpirience(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        String expirience = message.getText();
        if(!driversService.tryParseInt(expirience)){
            messageToSend.setText("Укажите целое число");
        }
        else{
            messageToSend.setText("Выберите класс автомобиля");
            messageToSend.setReplyMarkup(makeMarkupForAutoClass());
            specialMessagesMap.put(message.getChatId(),
                    TypeOfSpecialMessage.DRIVER_REGISTER_AUTO_CLASS);
            newDriverToRegister.setDriveExpirience(Integer.parseInt(expirience));
        }

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void driverRegistrationAutoClass(Message message){
        List<String> allClasses = autoClassesService.findAllClassesTitle();
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        String classChosen = message.getText();
        if(!allClasses.contains(classChosen)){
            messageToSend.setText("Выберите класс автомобиля из списка");
            messageToSend.setReplyMarkup(makeMarkupForAutoClass());
        }
        else{
            messageToSend.setText("Введите марку своего автомобиля");
            specialMessagesMap.put(message.getChatId(),
                    TypeOfSpecialMessage.DRIVER_REGISTER_AUTO_MARK);
            newDriverToRegister.setAutoClass(autoClassesService
                    .getAutoClassByTitle(classChosen));
        }

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void driverRegistrationAutoMark(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        String markTitle = message.getText().toUpperCase();
        Marks mark = marksService.getAndAddIfExcists(markTitle);
        messageToSend.setText("Введите цвет своего автомобиля");
        specialMessagesMap.put(message.getChatId(),
                TypeOfSpecialMessage.DRIVER_REGISTER_AUTO_COLOR);
        newDriverToRegister.setMark(mark);

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void driverRegistrationAutoColor(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        String colorTitle = message.getText().toUpperCase();
        Colors color = colorsService.getAndAddIfExcists(colorTitle);
        messageToSend.setText("Отправьте фото своего лица");
        specialMessagesMap.put(message.getChatId(),
                TypeOfSpecialMessage.DRIVER_REGISTER_PHOTO);
        newDriverToRegister.setColor(color);

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void driverRegistrationPhoto(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        if(!message.hasPhoto()){
            messageToSend.setText("Вы не отправили фото, попробуйте еще раз");
        }
        else{
            try {
                PhotoSize photo = message.getPhoto().get(0);
                String uuidForFile = UUID.randomUUID().toString();
                String fileName = photoPath + "/" + uuidForFile + "-" +
                        photo.getFileUniqueId();
                GetFile getFile = new GetFile(photo.getFileId());
                File file = execute(getFile);
                downloadFile(file, new java.io.File(fileName));
                messageToSend.setText("Вы успешно зарегестрировались");
                driversService.finishRegistration(newDriverToRegister, fileName);
                newDriverToRegister = new Drivers();
                specialMessagesMap.remove(message.getChatId());
            }
            catch (TelegramApiException e){
                e.printStackTrace();
            }
        }
        try {
            execute(messageToSend);
        }
        catch (TelegramApiException er){
            er.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup makeMarkupForAutoClass(){
        List<String> allClasses = autoClassesService.findAllClassesTitle();
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        allClasses.forEach(ac -> keyboardRow.add(ac));
        keyboardRows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

}
