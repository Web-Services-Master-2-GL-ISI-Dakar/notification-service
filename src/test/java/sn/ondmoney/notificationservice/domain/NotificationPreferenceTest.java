package sn.ondmoney.notificationservice.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.ondmoney.notificationservice.domain.NotificationPreferenceTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.ondmoney.notificationservice.web.rest.TestUtil;

class NotificationPreferenceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationPreference.class);
        NotificationPreference notificationPreference1 = getNotificationPreferenceSample1();
        NotificationPreference notificationPreference2 = new NotificationPreference();
        assertThat(notificationPreference1).isNotEqualTo(notificationPreference2);

        notificationPreference2.setId(notificationPreference1.getId());
        assertThat(notificationPreference1).isEqualTo(notificationPreference2);

        notificationPreference2 = getNotificationPreferenceSample2();
        assertThat(notificationPreference1).isNotEqualTo(notificationPreference2);
    }
}
