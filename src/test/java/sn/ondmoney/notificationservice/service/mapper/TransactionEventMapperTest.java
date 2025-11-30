package sn.ondmoney.notificationservice.service.mapper;

import static sn.ondmoney.notificationservice.domain.TransactionEventAsserts.*;
import static sn.ondmoney.notificationservice.domain.TransactionEventTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionEventMapperTest {

    private TransactionEventMapper transactionEventMapper;

    @BeforeEach
    void setUp() {
        transactionEventMapper = new TransactionEventMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTransactionEventSample1();
        var actual = transactionEventMapper.toEntity(transactionEventMapper.toDto(expected));
        assertTransactionEventAllPropertiesEquals(expected, actual);
    }
}
