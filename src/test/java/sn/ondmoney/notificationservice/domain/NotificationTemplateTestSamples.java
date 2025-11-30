package sn.ondmoney.notificationservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class NotificationTemplateTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static NotificationTemplate getNotificationTemplateSample1() {
        return new NotificationTemplate()
            .id(1L)
            .templateCode("templateCode1")
            .language("language1")
            .subject("subject1")
            .bodyTemplate("bodyTemplate1")
            .smsTemplate("smsTemplate1")
            .pushTitle("pushTitle1")
            .pushBody("pushBody1");
    }

    public static NotificationTemplate getNotificationTemplateSample2() {
        return new NotificationTemplate()
            .id(2L)
            .templateCode("templateCode2")
            .language("language2")
            .subject("subject2")
            .bodyTemplate("bodyTemplate2")
            .smsTemplate("smsTemplate2")
            .pushTitle("pushTitle2")
            .pushBody("pushBody2");
    }

    public static NotificationTemplate getNotificationTemplateRandomSampleGenerator() {
        return new NotificationTemplate()
            .id(longCount.incrementAndGet())
            .templateCode(UUID.randomUUID().toString())
            .language(UUID.randomUUID().toString())
            .subject(UUID.randomUUID().toString())
            .bodyTemplate(UUID.randomUUID().toString())
            .smsTemplate(UUID.randomUUID().toString())
            .pushTitle(UUID.randomUUID().toString())
            .pushBody(UUID.randomUUID().toString());
    }
}
