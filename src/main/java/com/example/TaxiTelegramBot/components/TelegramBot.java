package com.example.TaxiTelegramBot.components;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

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
            Message message = update.getMessage();
            String text = message.getText();
            switch(text){
                case "/start":
                    startMessage(message);
                    break;

                case "/help":
                    helpMessage(message);
                    break;

                default:
                    startMessage(message);
                    break;
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
                /register_as_driver - команда для регистрации нового пользователя
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

}
