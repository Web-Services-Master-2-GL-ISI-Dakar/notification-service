package sn.ondmoney.notificationservice.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.ondmoney.notificationservice.domain.NotificationLogTestSamples.*;
import static sn.ondmoney.notificationservice.domain.NotificationTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.ondmoney.notificationservice.web.rest.TestUtil;

class NotificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notification.class);
        Notification notification1 = getNotificationSample1();
        Notification notification2 = new Notification();
        assertThat(notification1).isNotEqualTo(notification2);

        notification2.setId(notification1.getId());
        assertThat(notification1).isEqualTo(notification2);

        notification2 = getNotificationSample2();
        assertThat(notification1).isNotEqualTo(notification2);
    }

    @Test
    void notificationLogTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        NotificationLog notificationLogBack = getNotificationLogRandomSampleGenerator();

        notification.addNotificationLog(notificationLogBack);
        assertThat(notification.getNotificationLogs()).containsOnly(notificationLogBack);
        assertThat(notificationLogBack.getNotification()).isEqualTo(notification);

        notification.removeNotificationLog(notificationLogBack);
        assertThat(notification.getNotificationLogs()).doesNotContain(notificationLogBack);
        assertThat(notificationLogBack.getNotification()).isNull();

        notification.notificationLogs(new HashSet<>(Set.of(notificationLogBack)));
        assertThat(notification.getNotificationLogs()).containsOnly(notificationLogBack);
        assertThat(notificationLogBack.getNotification()).isEqualTo(notification);

        notification.setNotificationLogs(new HashSet<>());
        assertThat(notification.getNotificationLogs()).doesNotContain(notificationLogBack);
        assertThat(notificationLogBack.getNotification()).isNull();
    }
}
