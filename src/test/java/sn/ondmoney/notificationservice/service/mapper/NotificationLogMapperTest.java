package sn.ondmoney.notificationservice.service.mapper;

import static sn.ondmoney.notificationservice.domain.NotificationLogAsserts.*;
import static sn.ondmoney.notificationservice.domain.NotificationLogTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationLogMapperTest {

    private NotificationLogMapper notificationLogMapper;

    @BeforeEach
    void setUp() {
        notificationLogMapper = new NotificationLogMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getNotificationLogSample1();
        var actual = notificationLogMapper.toEntity(notificationLogMapper.toDto(expected));
        assertNotificationLogAllPropertiesEquals(expected, actual);
    }
}
