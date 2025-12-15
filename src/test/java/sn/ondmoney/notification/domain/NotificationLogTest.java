package sn.ondmoney.notification.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.ondmoney.notification.domain.NotificationLogTestSamples.*;
import static sn.ondmoney.notification.domain.NotificationTemplateTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.ondmoney.notification.web.rest.TestUtil;

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
    void notificationTemplateUsedTest() {
        NotificationLog notificationLog = getNotificationLogRandomSampleGenerator();
        NotificationTemplate notificationTemplateBack = getNotificationTemplateRandomSampleGenerator();

        notificationLog.setNotificationTemplateUsed(notificationTemplateBack);
        assertThat(notificationLog.getNotificationTemplateUsed()).isEqualTo(notificationTemplateBack);

        notificationLog.notificationTemplateUsed(null);
        assertThat(notificationLog.getNotificationTemplateUsed()).isNull();
    }
}
