package sn.ondmoney.notification.service.mapper;

import static sn.ondmoney.notification.domain.NotificationTemplateAsserts.*;
import static sn.ondmoney.notification.domain.NotificationTemplateTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationTemplateMapperTest {

    private NotificationTemplateMapper notificationTemplateMapper;

    @BeforeEach
    void setUp() {
        notificationTemplateMapper = new NotificationTemplateMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getNotificationTemplateSample1();
        var actual = notificationTemplateMapper.toEntity(notificationTemplateMapper.toDto(expected));
        assertNotificationTemplateAllPropertiesEquals(expected, actual);
    }
}
