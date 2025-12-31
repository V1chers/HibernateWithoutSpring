package ru.aston;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.aston.event.NotificationEvent;

@Service
public class NotificationService {

    private final JavaMailSender emailSender;

    @Autowired
    public NotificationService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendNotification(NotificationEvent event) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom("user@gmail.com");
        mail.setTo(event.getEmail());
        if (event.getAction() == NotificationEvent.Action.CREATED) {
            mail.setSubject("Создание аккаунта");
            mail.setText("Здравствуйте, " + event.getName() + "! Ваш аккаунт на сайте был успешно создан.");
        } else if (event.getAction() == NotificationEvent.Action.DELETED) {
            mail.setSubject("Удаление аккаунта");
            mail.setText("Здравствуйте, " + event.getName() + "! Ваш аккаунт был удалён.");
        }

        emailSender.send(mail);
    }
}
