package sn.ondmoney.notificationservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.ondmoney.notificationservice.web.rest.TestUtil;

class NotificationRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationRequestDTO.class);
        NotificationRequestDTO notificationRequestDTO1 = new NotificationRequestDTO();
        notificationRequestDTO1.setId(1L);
        NotificationRequestDTO notificationRequestDTO2 = new NotificationRequestDTO();
        assertThat(notificationRequestDTO1).isNotEqualTo(notificationRequestDTO2);
        notificationRequestDTO2.setId(notificationRequestDTO1.getId());
        assertThat(notificationRequestDTO1).isEqualTo(notificationRequestDTO2);
        notificationRequestDTO2.setId(2L);
        assertThat(notificationRequestDTO1).isNotEqualTo(notificationRequestDTO2);
        notificationRequestDTO1.setId(null);
        assertThat(notificationRequestDTO1).isNotEqualTo(notificationRequestDTO2);
    }
}
