package sn.ondmoney.notificationservice.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TransactionEventTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TransactionEvent getTransactionEventSample1() {
        return new TransactionEvent()
            .id(1L)
            .transactionId("transactionId1")
            .senderAccount("senderAccount1")
            .receiverAccount("receiverAccount1")
            .additionalData("additionalData1");
    }

    public static TransactionEvent getTransactionEventSample2() {
        return new TransactionEvent()
            .id(2L)
            .transactionId("transactionId2")
            .senderAccount("senderAccount2")
            .receiverAccount("receiverAccount2")
            .additionalData("additionalData2");
    }

    public static TransactionEvent getTransactionEventRandomSampleGenerator() {
        return new TransactionEvent()
            .id(longCount.incrementAndGet())
            .transactionId(UUID.randomUUID().toString())
            .senderAccount(UUID.randomUUID().toString())
            .receiverAccount(UUID.randomUUID().toString())
            .additionalData(UUID.randomUUID().toString());
    }
}
