package com.example.TaxiTelegramBot.components;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
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
            if(update.getMessage().getText().equals("/start")){
                sendMessage(update.getMessage().getChatId());
            }
        }
    }

    private void sendMessage(long chatId){

        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(String.valueOf(chatId));

        try{
            execute(message);
        }
        catch(TelegramApiException e){
            throw new RuntimeException(e);
        }
    }

    public void sendOrder(long orderId){

        SendMessage message = new SendMessage();
        message.setChatId(5610194564L);
        message.setText(String.valueOf("Новый заказ!!! ID заказа: " + orderId));

        try{
            execute(message);
        }
        catch(TelegramApiException e){
            throw new RuntimeException(e);
        }
    }


}
