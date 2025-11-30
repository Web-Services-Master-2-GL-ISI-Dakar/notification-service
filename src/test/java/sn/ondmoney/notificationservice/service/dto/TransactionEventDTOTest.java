package sn.ondmoney.notificationservice.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.ondmoney.notificationservice.web.rest.TestUtil;

class TransactionEventDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionEventDTO.class);
        TransactionEventDTO transactionEventDTO1 = new TransactionEventDTO();
        transactionEventDTO1.setId(1L);
        TransactionEventDTO transactionEventDTO2 = new TransactionEventDTO();
        assertThat(transactionEventDTO1).isNotEqualTo(transactionEventDTO2);
        transactionEventDTO2.setId(transactionEventDTO1.getId());
        assertThat(transactionEventDTO1).isEqualTo(transactionEventDTO2);
        transactionEventDTO2.setId(2L);
        assertThat(transactionEventDTO1).isNotEqualTo(transactionEventDTO2);
        transactionEventDTO1.setId(null);
        assertThat(transactionEventDTO1).isNotEqualTo(transactionEventDTO2);
    }
}
