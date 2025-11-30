package sn.ondmoney.notificationservice.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

/**
 * DTO pour recevoir les événements de transaction depuis Kafka
 * Ce DTO correspond aux messages publiés par le microservice Transaction
 */
public class TransactionEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String transactionId;
    private String senderUserId;
    private String senderAccount;
    private String receiverUserId;
    private String receiverAccount;
    private BigDecimal amount;
    private String currency;
    private String type; // SEND, RECEIVE, WITHDRAWAL, DEPOSIT
    private String status; // SUCCESS, FAILED, PENDING
    private Instant timestamp;
    private Map<String, Object> additionalData;

    // Constructeurs
    public TransactionEventDTO() {}

    public TransactionEventDTO(
        String transactionId,
        String senderUserId,
        String senderAccount,
        String receiverUserId,
        String receiverAccount,
        BigDecimal amount
    ) {
        this.transactionId = transactionId;
        this.senderUserId = senderUserId;
        this.senderAccount = senderAccount;
        this.receiverUserId = receiverUserId;
        this.receiverAccount = receiverAccount;
        this.amount = amount;
    }

    // Getters et Setters
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(String senderAccount) {
        this.senderAccount = senderAccount;
    }

    public String getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(String receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public String getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(String receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }

    @Override
    public String toString() {
        return (
            "TransactionEventDTO{" +
            "transactionId='" +
            transactionId +
            '\'' +
            ", senderUserId='" +
            senderUserId +
            '\'' +
            ", receiverUserId='" +
            receiverUserId +
            '\'' +
            ", amount=" +
            amount +
            ", type='" +
            type +
            '\'' +
            ", status='" +
            status +
            '\'' +
            '}'
        );
    }
}
