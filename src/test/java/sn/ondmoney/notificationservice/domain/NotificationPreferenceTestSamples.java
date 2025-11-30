package sn.ondmoney.notificationservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class NotificationPreferenceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static NotificationPreference getNotificationPreferenceSample1() {
        return new NotificationPreference().id(1L).userId("userId1").mutedTypes("mutedTypes1").language("language1");
    }

    public static NotificationPreference getNotificationPreferenceSample2() {
        return new NotificationPreference().id(2L).userId("userId2").mutedTypes("mutedTypes2").language("language2");
    }

    public static NotificationPreference getNotificationPreferenceRandomSampleGenerator() {
        return new NotificationPreference()
            .id(longCount.incrementAndGet())
            .userId(UUID.randomUUID().toString())
            .mutedTypes(UUID.randomUUID().toString())
            .language(UUID.randomUUID().toString());
    }
}
