package ru.aston;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.aston.event.NotificationEvent;

@Component
@KafkaListener(topics = "notification")
public class NotificationConsumer {

    private final NotificationService notificationService;

    public NotificationConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaHandler
    public void consumeNotificationEvent(NotificationEvent event) {
        notificationService.sendNotification(event);
    }
}
