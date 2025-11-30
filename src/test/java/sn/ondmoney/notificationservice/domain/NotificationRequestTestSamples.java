package sn.ondmoney.notificationservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class NotificationRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static NotificationRequest getNotificationRequestSample1() {
        return new NotificationRequest().id(1L).userId("userId1").accountNumber("accountNumber1").channels("channels1").data("data1");
    }

    public static NotificationRequest getNotificationRequestSample2() {
        return new NotificationRequest().id(2L).userId("userId2").accountNumber("accountNumber2").channels("channels2").data("data2");
    }

    public static NotificationRequest getNotificationRequestRandomSampleGenerator() {
        return new NotificationRequest()
            .id(longCount.incrementAndGet())
            .userId(UUID.randomUUID().toString())
            .accountNumber(UUID.randomUUID().toString())
            .channels(UUID.randomUUID().toString())
            .data(UUID.randomUUID().toString());
    }
}
