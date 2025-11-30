package sn.ondmoney.notificationservice.service.mapper;

import static sn.ondmoney.notificationservice.domain.NotificationPreferenceAsserts.*;
import static sn.ondmoney.notificationservice.domain.NotificationPreferenceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationPreferenceMapperTest {

    private NotificationPreferenceMapper notificationPreferenceMapper;

    @BeforeEach
    void setUp() {
        notificationPreferenceMapper = new NotificationPreferenceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getNotificationPreferenceSample1();
        var actual = notificationPreferenceMapper.toEntity(notificationPreferenceMapper.toDto(expected));
        assertNotificationPreferenceAllPropertiesEquals(expected, actual);
    }
}
