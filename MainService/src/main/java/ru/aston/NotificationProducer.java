package ru.aston;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import ru.aston.event.NotificationEvent;

@Component
public class NotificationProducer {

    private final KafkaTemplate<Integer, NotificationEvent> kafkaTemplate;

    @Autowired
    public NotificationProducer(KafkaTemplate<Integer, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public SendResult<Integer, NotificationEvent> sendNotification(Users user, NotificationEvent.Action action) {
        NotificationEvent notificationEvent = new NotificationEvent(
                user.getId(),
                user.getName(),
                user.getEmail(),
                action
        );

        try {
            return kafkaTemplate.send("notification", user.getId(), notificationEvent).get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
