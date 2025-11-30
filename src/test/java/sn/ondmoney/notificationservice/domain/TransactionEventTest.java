package sn.ondmoney.notificationservice.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.ondmoney.notificationservice.domain.TransactionEventTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.ondmoney.notificationservice.web.rest.TestUtil;

class TransactionEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionEvent.class);
        TransactionEvent transactionEvent1 = getTransactionEventSample1();
        TransactionEvent transactionEvent2 = new TransactionEvent();
        assertThat(transactionEvent1).isNotEqualTo(transactionEvent2);

        transactionEvent2.setId(transactionEvent1.getId());
        assertThat(transactionEvent1).isEqualTo(transactionEvent2);

        transactionEvent2 = getTransactionEventSample2();
        assertThat(transactionEvent1).isNotEqualTo(transactionEvent2);
    }
}
