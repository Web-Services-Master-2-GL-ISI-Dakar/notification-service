package sn.ondmoney.notificationservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserProfile getUserProfileSample1() {
        return new UserProfile().id(1L).userId("userId1").phoneNumber("phoneNumber1").email("email1").deviceToken("deviceToken1");
    }

    public static UserProfile getUserProfileSample2() {
        return new UserProfile().id(2L).userId("userId2").phoneNumber("phoneNumber2").email("email2").deviceToken("deviceToken2");
    }

    public static UserProfile getUserProfileRandomSampleGenerator() {
        return new UserProfile()
            .id(longCount.incrementAndGet())
            .userId(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .deviceToken(UUID.randomUUID().toString());
    }
}
