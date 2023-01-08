package com.lotby.webhookstripe.services;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.lotby.webhookstripe.models.Notification;
import com.lotby.webhookstripe.repositories.NotificationRepository;

@Service
public class NotificationService {

    private NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> addNotification(String chatId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("chatId").is(chatId));
        List<Notification> notificationForChatId = this.notificationRepository.findByChatId(chatId);
        if ( notificationForChatId.size() == 0 ) {
            Notification newNotificationSubs = new Notification();
            newNotificationSubs.setChatId(chatId);
            this.notificationRepository.save(newNotificationSubs);
        
            return notificationForChatId;
        }
        // Already exist
        return null;
    }
}
