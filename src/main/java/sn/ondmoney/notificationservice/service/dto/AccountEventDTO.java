package sn.ondmoney.notificationservice.service.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO pour les événements de compte (création, modification, etc.)
 */
public class AccountEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accountId;
    private String userId;
    private String accountNumber;
    private String eventType; // CREATED, ACTIVATED, SUSPENDED, CLOSED
    private String phoneNumber;
    private String email;
    private Instant timestamp;

    // Constructeurs
    public AccountEventDTO() {}

    // Getters et Setters
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return (
            "AccountEventDTO{" + "accountId='" + accountId + '\'' + ", userId='" + userId + '\'' + ", eventType='" + eventType + '\'' + '}'
        );
    }
}
