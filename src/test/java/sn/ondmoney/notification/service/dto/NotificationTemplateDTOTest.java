package sn.ondmoney.notification.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.ondmoney.notification.web.rest.TestUtil;

class NotificationTemplateDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationTemplateDTO.class);
        NotificationTemplateDTO notificationTemplateDTO1 = new NotificationTemplateDTO();
        notificationTemplateDTO1.setId("id1");
        NotificationTemplateDTO notificationTemplateDTO2 = new NotificationTemplateDTO();
        assertThat(notificationTemplateDTO1).isNotEqualTo(notificationTemplateDTO2);
        notificationTemplateDTO2.setId(notificationTemplateDTO1.getId());
        assertThat(notificationTemplateDTO1).isEqualTo(notificationTemplateDTO2);
        notificationTemplateDTO2.setId("id2");
        assertThat(notificationTemplateDTO1).isNotEqualTo(notificationTemplateDTO2);
        notificationTemplateDTO1.setId(null);
        assertThat(notificationTemplateDTO1).isNotEqualTo(notificationTemplateDTO2);
    }
}
