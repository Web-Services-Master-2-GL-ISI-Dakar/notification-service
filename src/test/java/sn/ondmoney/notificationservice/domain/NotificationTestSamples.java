package sn.ondmoney.notificationservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NotificationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Notification getNotificationSample1() {
        return new Notification()
            .id(1L)
            .userId("userId1")
            .accountNumber("accountNumber1")
            .title("title1")
            .message("message1")
            .recipient("recipient1")
            .errorMessage("errorMessage1")
            .retryCount(1)
            .metadata("metadata1");
    }

    public static Notification getNotificationSample2() {
        return new Notification()
            .id(2L)
            .userId("userId2")
            .accountNumber("accountNumber2")
            .title("title2")
            .message("message2")
            .recipient("recipient2")
            .errorMessage("errorMessage2")
            .retryCount(2)
            .metadata("metadata2");
    }

    public static Notification getNotificationRandomSampleGenerator() {
        return new Notification()
            .id(longCount.incrementAndGet())
            .userId(UUID.randomUUID().toString())
            .accountNumber(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString())
            .message(UUID.randomUUID().toString())
            .recipient(UUID.randomUUID().toString())
            .errorMessage(UUID.randomUUID().toString())
            .retryCount(intCount.incrementAndGet())
            .metadata(UUID.randomUUID().toString());
    }
}
