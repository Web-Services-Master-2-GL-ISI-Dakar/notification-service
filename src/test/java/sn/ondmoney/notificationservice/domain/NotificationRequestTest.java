package sn.ondmoney.notificationservice.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.ondmoney.notificationservice.domain.NotificationRequestTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.ondmoney.notificationservice.web.rest.TestUtil;

class NotificationRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationRequest.class);
        NotificationRequest notificationRequest1 = getNotificationRequestSample1();
        NotificationRequest notificationRequest2 = new NotificationRequest();
        assertThat(notificationRequest1).isNotEqualTo(notificationRequest2);

        notificationRequest2.setId(notificationRequest1.getId());
        assertThat(notificationRequest1).isEqualTo(notificationRequest2);

        notificationRequest2 = getNotificationRequestSample2();
        assertThat(notificationRequest1).isNotEqualTo(notificationRequest2);
    }
}
