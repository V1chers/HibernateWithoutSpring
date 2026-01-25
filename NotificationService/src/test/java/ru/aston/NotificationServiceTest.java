package ru.aston;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.google.common.base.Charsets;
import ru.aston.event.NotificationEvent;

import static com.icegreen.greenmail.configuration.GreenMailConfiguration.aConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class NotificationServiceTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(aConfig().withUser("user", "pass"))
            .withPerMethodLifecycle(false);
    @Autowired
    private NotificationService notificationService;

    @BeforeEach
    public void init() throws Exception {
        greenMail.purgeEmailFromAllMailboxes();
    }

    @Test
    void sendNotification_withActionCreated_shouldSendNotification() throws Exception {
        NotificationEvent notificationEvent = createNotificationEvent();
        notificationEvent.setAction(NotificationEvent.Action.CREATED);

        notificationService.sendNotification(notificationEvent);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        assertEquals("Создание аккаунта", message.getSubject());
        assertEquals(notificationEvent.getEmail(), message.getAllRecipients()[0].toString());

        byte[] data = Base64.decodeBase64(GreenMailUtil.getBody(message));
        String returnedMessage = new String(data, Charsets.UTF_8);
        assertEquals("Здравствуйте, " + notificationEvent.getName() + "! Ваш аккаунт на сайте был успешно создан.", returnedMessage);
    }

    @Test
    void sendNotification_withActionDeleted_shouldSendNotification() throws Exception {
        NotificationEvent notificationEvent = createNotificationEvent();

        notificationService.sendNotification(notificationEvent);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);

        MimeMessage message = receivedMessages[0];
        assertEquals("Удаление аккаунта", message.getSubject());
        assertEquals(notificationEvent.getEmail(), message.getAllRecipients()[0].toString());

        byte[] data = Base64.decodeBase64(GreenMailUtil.getBody(message));
        String returnedMessage = new String(data, Charsets.UTF_8);
        assertEquals("Здравствуйте, " + notificationEvent.getName() + "! Ваш аккаунт был удалён.", returnedMessage);
    }

    public NotificationEvent createNotificationEvent() {
        return new NotificationEvent(
                null,
                "Oleg",
                "Oleg@gmail.com",
                NotificationEvent.Action.DELETED
        );
    }
}
