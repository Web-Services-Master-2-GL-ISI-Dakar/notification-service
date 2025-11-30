package sn.ondmoney.notificationservice.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.ondmoney.notificationservice.domain.NotificationLogTestSamples.*;
import static sn.ondmoney.notificationservice.domain.NotificationTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.ondmoney.notificationservice.web.rest.TestUtil;

class NotificationLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationLog.class);
        NotificationLog notificationLog1 = getNotificationLogSample1();
        NotificationLog notificationLog2 = new NotificationLog();
        assertThat(notificationLog1).isNotEqualTo(notificationLog2);

        notificationLog2.setId(notificationLog1.getId());
        assertThat(notificationLog1).isEqualTo(notificationLog2);

        notificationLog2 = getNotificationLogSample2();
        assertThat(notificationLog1).isNotEqualTo(notificationLog2);
    }

    @Test
    void notificationTest() {
        NotificationLog notificationLog = getNotificationLogRandomSampleGenerator();
        Notification notificationBack = getNotificationRandomSampleGenerator();

        notificationLog.setNotification(notificationBack);
        assertThat(notificationLog.getNotification()).isEqualTo(notificationBack);

        notificationLog.notification(null);
        assertThat(notificationLog.getNotification()).isNull();
    }
}
