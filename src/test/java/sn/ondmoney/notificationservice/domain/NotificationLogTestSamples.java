package sn.ondmoney.notificationservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NotificationLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static NotificationLog getNotificationLogSample1() {
        return new NotificationLog()
            .id(1L)
            .notificationId(1L)
            .userId("userId1")
            .message("message1")
            .recipient("recipient1")
            .retryCount(1)
            .channelResults("channelResults1")
            .action("action1")
            .details("details1");
    }

    public static NotificationLog getNotificationLogSample2() {
        return new NotificationLog()
            .id(2L)
            .notificationId(2L)
            .userId("userId2")
            .message("message2")
            .recipient("recipient2")
            .retryCount(2)
            .channelResults("channelResults2")
            .action("action2")
            .details("details2");
    }

    public static NotificationLog getNotificationLogRandomSampleGenerator() {
        return new NotificationLog()
            .id(longCount.incrementAndGet())
            .notificationId(longCount.incrementAndGet())
            .userId(UUID.randomUUID().toString())
            .message(UUID.randomUUID().toString())
            .recipient(UUID.randomUUID().toString())
            .retryCount(intCount.incrementAndGet())
            .channelResults(UUID.randomUUID().toString())
            .action(UUID.randomUUID().toString())
            .details(UUID.randomUUID().toString());
    }
}
