package sn.ondmoney.notification.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.ondmoney.notification.domain.NotificationTemplateTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.ondmoney.notification.web.rest.TestUtil;

class NotificationTemplateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationTemplate.class);
        NotificationTemplate notificationTemplate1 = getNotificationTemplateSample1();
        NotificationTemplate notificationTemplate2 = new NotificationTemplate();
        assertThat(notificationTemplate1).isNotEqualTo(notificationTemplate2);

        notificationTemplate2.setId(notificationTemplate1.getId());
        assertThat(notificationTemplate1).isEqualTo(notificationTemplate2);

        notificationTemplate2 = getNotificationTemplateSample2();
        assertThat(notificationTemplate1).isNotEqualTo(notificationTemplate2);
    }
}
