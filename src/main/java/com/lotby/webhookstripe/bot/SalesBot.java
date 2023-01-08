package com.lotby.webhookstripe.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;


// Telegram starter spring boot working on 2.5.0 ( not in 3.0.1 !)

@Component
public class SalesBot extends TelegramLongPollingBot  {


    @Value("${telegram.botToken}")
    private String botToken;

    @Value("${telegram.botName}")
    private String botUsername;

    @Override
    public void onUpdateReceived(Update update) {
        if ( update.hasMessage() ) {
            System.out.println("Message: " + update.getMessage().getText());
        }
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }

    @Override
    public String getBotUsername() {
        // Return bot username
        // If bot username is @MyAmazingBot, it must return 'MyAmazingBot'
        return this.botUsername;
    }
    
}
