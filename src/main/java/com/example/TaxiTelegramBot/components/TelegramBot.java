package com.example.TaxiTelegramBot.components;


import com.example.TaxiTelegramBot.config.PhotoPathConfig;
import com.example.TaxiTelegramBot.entities.*;
import com.example.TaxiTelegramBot.enums.TypeOfSpecialMessage;
import com.example.TaxiTelegramBot.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final PhotoPathConfig photoPathConfig;

    private ConcurrentHashMap<Long, TypeOfSpecialMessage> specialMessagesMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Long, Users> newUsersMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Long, Drivers> newDriversMap = new ConcurrentHashMap<>();

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
        if(update.hasMessage()){
            if(update.getMessage().hasPhoto() && specialMessagesMap
                    .containsKey(update.getMessage().getChatId())){
                specialMessagesHandler(update.getMessage(),
                        specialMessagesMap.get(update.getMessage().getChatId()));
            }
            else if(update.getMessage().hasText()){
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

                        case "/login_as_driver":
                            startLoginAsDriver(message);
                            break;

                        case "/logout_as_driver":
                            logoutAsDriver(message);
                            break;

                        case "/user_money":
                            userMoney(message);
                            break;

                        case "/driver_money":
                            driverMoney(message);
                            break;

                        default:
                            defaultMessage(message);
                            break;
                    }
                }
            }
        }
        else if(update.hasCallbackQuery()){
            String callback = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            switch (callback){
                case "ADD_MONEY_USER":
                    addUserMoney(chatId, messageId);
                    break;

                case "HIDE_MONEY":
                    hideMoney(chatId, messageId);
                    break;

                case "GET_MONEY_DRIVER":
                    getDriverMoney(chatId, messageId);
                    break;

                default:
                    break;

            }
        }
    }

    private void specialMessagesHandler(Message message, TypeOfSpecialMessage type){
        switch (type){
            case TypeOfSpecialMessage.USER_REGISTER_LOGIN:
                userRegisterLogin(message);
                break;

            case TypeOfSpecialMessage.USER_REGISTER_PASSWORD:
                userRegisterPassword(message);
                break;

            case TypeOfSpecialMessage.USER_REGISTER_FIO:
                userRegisterFIO(message);
                break;

            case TypeOfSpecialMessage.USER_REGISTER_CITY:
                userRegisterCity(message);
                break;

            case TypeOfSpecialMessage.USER_AUTORIZE:
                userAutorize(message);
                break;

            case TypeOfSpecialMessage.DRIVER_REGISTER_LOGIN:
                driverRegistrationLogin(message);
                break;

            case TypeOfSpecialMessage.DRIVER_REGISTER_PASSWORD:
                driverRegistrationPassword(message);
                break;

            case TypeOfSpecialMessage.DRIVER_REGISTER_FIO:
                driverRegistrationFio(message);
                break;

            case TypeOfSpecialMessage.DRIVER_REGISTER_CITY:
                driverRegistrationCity(message);
                break;

            case TypeOfSpecialMessage.DRIVER_REGISTER_AUTO_NUMBER:
                driverRegistrationAutoNumber(message);
                break;

            case TypeOfSpecialMessage.DRIVER_REGISTER_DRIVE_EXPIRIENCE:
                driverRegistrationDriveExpirience(message);
                break;

            case TypeOfSpecialMessage.DRIVER_REGISTER_AUTO_CLASS:
                driverRegistrationAutoClass(message);
                break;

            case TypeOfSpecialMessage.DRIVER_REGISTER_AUTO_MARK:
                driverRegistrationAutoMark(message);
                break;

            case TypeOfSpecialMessage.DRIVER_REGISTER_AUTO_COLOR:
                driverRegistrationAutoColor(message);
                break;

            case TypeOfSpecialMessage.DRIVER_REGISTER_PHOTO:
                driverRegistrationPhoto(message);
                break;

            case TypeOfSpecialMessage.DRIVER_AUTORIZE:
                driverAutorize(message);
                break;

            case TypeOfSpecialMessage.DRIVER_GET_MONEY:
                driverHowMuchMoneyToGet(message);
                break;

            default:
                break;
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
                /user_money
                /driver_money
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
            newUsersMap.put(message.getChatId(), new Users());
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
            Users user = newUsersMap.get(message.getChatId());
            user.setLogin(login);
            newUsersMap.put(message.getChatId(), user);
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
        Users user = newUsersMap.get(message.getChatId());
        user.setPassword(usersService.hashPassword(password));
        newUsersMap.put(message.getChatId(), user);

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
        Users user = newUsersMap.get(message.getChatId());
        user.setFio(fio);
        newUsersMap.put(message.getChatId(), user);

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
        Users user = newUsersMap.get(message.getChatId());
        user.setCity(realCity);
        user.setChatId(message.getChatId());
        usersService.registerNewUser(user);
        newUsersMap.remove(message.getChatId());
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
        String[] loginAndPassword = message.getText().split(" ");
        if(loginAndPassword.length != 2){
            messageToSend.setText("Вы ввели данные неверно");
        }
        else{
            String answerFromServer = usersService
                    .loginUser(message.getChatId(),
                            loginAndPassword[0], loginAndPassword[1]);
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
            newDriversMap.put(message.getChatId(), new Drivers());
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
            Drivers driver = newDriversMap.get(message.getChatId());
            driver.setLogin(login);
            newDriversMap.put(message.getChatId(), driver);
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
        Drivers driver = newDriversMap.get(message.getChatId());
        driver.setPassword(password);
        newDriversMap.put(message.getChatId(), driver);

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
        Drivers driver = newDriversMap.get(message.getChatId());
        driver.setFio(message.getText());
        newDriversMap.put(message.getChatId(), driver);

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
        Drivers driver = newDriversMap.get(message.getChatId());
        driver.setCity(city);
        newDriversMap.put(message.getChatId(), driver);
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
            Drivers driver = newDriversMap.get(message.getChatId());
            driver.setAutoNumber(number);
            newDriversMap.put(message.getChatId(), driver);
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
            Drivers driver = newDriversMap.get(message.getChatId());
            driver.setDriveExpirience(Integer.parseInt(expirience));
            newDriversMap.put(message.getChatId(), driver);
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
            Drivers driver = newDriversMap.get(message.getChatId());
            driver.setAutoClass(autoClassesService.getAutoClassByTitle(classChosen));
            newDriversMap.put(message.getChatId(), driver);
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
        Drivers driver = newDriversMap.get(message.getChatId());
        driver.setMark(mark);
        newDriversMap.put(message.getChatId(), driver);

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
        Drivers driver = newDriversMap.get(message.getChatId());
        driver.setColor(color);
        newDriversMap.put(message.getChatId(), driver);

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
                String fileName = photoPathConfig.getPhotoPath() + "/" + uuidForFile + "-" +
                        photo.getFileUniqueId() + ".jpg";
                GetFile getFile = new GetFile(photo.getFileId());
                File file = execute(getFile);
                downloadFile(file, new java.io.File(fileName));
                messageToSend.setText("Вы успешно зарегестрировались");
                Drivers driver = newDriversMap.get(message.getChatId());
                driver.setChatId(message.getChatId());
                driversService.finishRegistration(driver, fileName);
                newDriversMap.remove(message.getChatId());
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

    private void startLoginAsDriver(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        if(driversService.checkDriverLoginOrNot(message.getChatId())){
            messageToSend.setText("Вы уже вошли в аккаунт водителя");
        }
        else{
            messageToSend.setText("Введите логин и пароль через пробел");
            specialMessagesMap.put(message.getChatId(),
                    TypeOfSpecialMessage.DRIVER_AUTORIZE);
        }

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void driverAutorize(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        messageToSend.setText(driversService.loginDriver(message.getText(),
                message.getChatId()));
        specialMessagesMap.remove(message.getChatId());

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void logoutAsDriver(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        if(!driversService.checkDriverLoginOrNot(message.getChatId())){
            messageToSend.setText("Вы еще не вошли в аккаунт водителя");
        }
        else{
            driversService.logoutDriver(message.getChatId());
            messageToSend.setText("Вы вышли из аккаунта водителя");
        }

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void userMoney(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        if(!usersService.checkUserLoginOrNot(message.getChatId())){
            messageToSend.setText("Вы еще не залогинились как пользователь");
        }
        else{
            String userMoney = usersService.getUserMoney(message.getChatId());
            messageToSend.setText("Ваш баланс: " + userMoney + "₽");
            messageToSend.setReplyMarkup(makeMarkupForUserMoney());
        }

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void driverMoney(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        if(!driversService.checkDriverLoginOrNot(message.getChatId())){
            messageToSend.setText("Вы еще не залогинились как водитель");
        }
        else{
            String driverMoney = driversService.getDriverMoney(message.getChatId());
            messageToSend.setText("Ваш баланс: " + driverMoney + "₽");
            messageToSend.setReplyMarkup(makeMarkupForDriverMoney());
        }

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void addUserMoney(long chatId, long messageId){
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId((int) messageId);
        usersService.addMoneyToBalance(chatId, "100");
        editMessageText.setText("Здесь должно быть апи, но его нет, вам начислили 100.00₽");

        try{
            execute(editMessageText);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void hideMoney(long chatId, long messageId){
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId((int) messageId);
        editMessageText.setText("Баланс скрыт");

        try{
            execute(editMessageText);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void getDriverMoney(long chatId, long messageId) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId((int) messageId);
        editMessageText.setText("Введите сколько денег вы хотите вывести");
        specialMessagesMap.put(chatId, TypeOfSpecialMessage.DRIVER_GET_MONEY);

        try{
            execute(editMessageText);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    private void driverHowMuchMoneyToGet(Message message){
        SendMessage messageToSend = new SendMessage();
        messageToSend.setChatId(message.getChatId());
        messageToSend.setText(driversService.getMoneyFromDriverBalance(message.getChatId(),
                message.getText()));
        specialMessagesMap.remove(message.getChatId());

        try{
            execute(messageToSend);
        }
        catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }


    private InlineKeyboardMarkup makeMarkupForUserMoney(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> buttonsInRow = new ArrayList<>();

        InlineKeyboardButton firstButton =
                new InlineKeyboardButton("Добавить денег на баланс");
        firstButton.setCallbackData("ADD_MONEY_USER");
        InlineKeyboardButton secondButton =
                new InlineKeyboardButton("Скрыть баланс");
        secondButton.setCallbackData("HIDE_MONEY");

        buttonsInRow.add(firstButton);
        buttonsInRow.add(secondButton);
        rowsInlineButtons.add(buttonsInRow);
        inlineKeyboardMarkup.setKeyboard(rowsInlineButtons);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardMarkup makeMarkupForDriverMoney(){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> buttonsInRow = new ArrayList<>();

        InlineKeyboardButton firstButton =
                new InlineKeyboardButton("Вывести деньги с баланса");
        firstButton.setCallbackData("GET_MONEY_DRIVER");
        InlineKeyboardButton secondButton =
                new InlineKeyboardButton("Скрыть баланс");
        secondButton.setCallbackData("HIDE_MONEY");

        buttonsInRow.add(firstButton);
        buttonsInRow.add(secondButton);
        rowsInlineButtons.add(buttonsInRow);
        inlineKeyboardMarkup.setKeyboard(rowsInlineButtons);
        return inlineKeyboardMarkup;
    }


}
