package sn.ondmoney.notificationservice.service.mapper;

import static sn.ondmoney.notificationservice.domain.NotificationRequestAsserts.*;
import static sn.ondmoney.notificationservice.domain.NotificationRequestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationRequestMapperTest {

    private NotificationRequestMapper notificationRequestMapper;

    @BeforeEach
    void setUp() {
        notificationRequestMapper = new NotificationRequestMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getNotificationRequestSample1();
        var actual = notificationRequestMapper.toEntity(notificationRequestMapper.toDto(expected));
        assertNotificationRequestAllPropertiesEquals(expected, actual);
    }
}
