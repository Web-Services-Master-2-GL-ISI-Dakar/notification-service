package sn.ondmoney.notification.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static NotificationLog getNotificationLogSample1() {
        return new NotificationLog()
            .id("id1")
            .eventRef("eventRef1")
            .userId("userId1")
            .recipient("recipient1")
            .externalEventRef("externalEventRef1")
            .errorMessage("errorMessage1")
            .retryCount(1);
    }

    public static NotificationLog getNotificationLogSample2() {
        return new NotificationLog()
            .id("id2")
            .eventRef("eventRef2")
            .userId("userId2")
            .recipient("recipient2")
            .externalEventRef("externalEventRef2")
            .errorMessage("errorMessage2")
            .retryCount(2);
    }

    public static NotificationLog getNotificationLogRandomSampleGenerator() {
        return new NotificationLog()
            .id(UUID.randomUUID().toString())
            .eventRef(UUID.randomUUID().toString())
            .userId(UUID.randomUUID().toString())
            .recipient(UUID.randomUUID().toString())
            .externalEventRef(UUID.randomUUID().toString())
            .errorMessage(UUID.randomUUID().toString())
            .retryCount(intCount.incrementAndGet());
    }
}
