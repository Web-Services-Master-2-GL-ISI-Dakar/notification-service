package sn.ondmoney.notificationservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.ondmoney.notificationservice.web.rest.TestUtil;

class NotificationLogDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationLogDTO.class);
        NotificationLogDTO notificationLogDTO1 = new NotificationLogDTO();
        notificationLogDTO1.setId(1L);
        NotificationLogDTO notificationLogDTO2 = new NotificationLogDTO();
        assertThat(notificationLogDTO1).isNotEqualTo(notificationLogDTO2);
        notificationLogDTO2.setId(notificationLogDTO1.getId());
        assertThat(notificationLogDTO1).isEqualTo(notificationLogDTO2);
        notificationLogDTO2.setId(2L);
        assertThat(notificationLogDTO1).isNotEqualTo(notificationLogDTO2);
        notificationLogDTO1.setId(null);
        assertThat(notificationLogDTO1).isNotEqualTo(notificationLogDTO2);
    }
}
