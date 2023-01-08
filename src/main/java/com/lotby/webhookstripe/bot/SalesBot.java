package com.lotby.webhookstripe.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import com.lotby.webhookstripe.services.NotificationService;


// Telegram starter spring boot working on 2.5.0 ( not in 3.0.1 !)

@Component
public class SalesBot extends TelegramLongPollingBot  {
    
    Logger logger = LoggerFactory.getLogger(SalesBot.class);

    private NotificationService notificationService;

    public SalesBot (NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Value("${telegram.botToken}")
    private String botToken;

    @Value("${telegram.botName}")
    private String botUsername;

    @Override
    public void onUpdateReceived(Update update) {
        if ( update.hasMessage() ) {
            try {
                String id = update.getMessage().getChatId().toString();

                switch ( update.getMessage().getText() ) {

                    case "/subscribe":
        
                            var result = this.notificationService.addNotification(id);

                            if ( result == null ) {
                                this.sendNotification(id, "You are already subscribed to notifications.");
                                break;
                            }
                            
                            this.sendNotification(id, "You are subscribed to notifications !");
                        break;
                    default:
                        System.out.println("Unsupported command");
                        this.sendNotification(id, "Unsupported command. Do you mean /subscribe ?");
                        break;
                }
            } catch ( Exception e ){
                this.logger.info("Error: " + e.getMessage());
            }

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

    public void sendNotification(String chatId, String msg) throws TelegramApiException {
        SendMessage response = new SendMessage(chatId, msg);
        execute(response);
    }
    
}
