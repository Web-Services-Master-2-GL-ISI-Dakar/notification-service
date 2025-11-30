package sn.ondmoney.notificationservice.service.dto;

import java.io.Serializable;
import java.util.List;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationChannel;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationType;
import sn.ondmoney.notificationservice.domain.enumeration.Priority;

/**
 * DTO pour les requêtes de notification directe
 * Utilisé par l'endpoint SOAP
 */
public class NotificationRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String accountNumber;
    private NotificationType type;
    private List<NotificationChannel> channels;
    private String title;
    private String message;
    private Priority priority;
    private Boolean immediate;
    private String metadata;

    // Constructeurs
    public NotificationRequestDTO() {}

    // Getters et Setters
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

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public List<NotificationChannel> getChannels() {
        return channels;
    }

    public void setChannels(List<NotificationChannel> channels) {
        this.channels = channels;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Boolean getImmediate() {
        return immediate;
    }

    public void setImmediate(Boolean immediate) {
        this.immediate = immediate;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return (
            "NotificationRequestDTO{" +
            "userId='" +
            userId +
            '\'' +
            ", type=" +
            type +
            ", channels=" +
            channels +
            ", priority=" +
            priority +
            ", immediate=" +
            immediate +
            '}'
        );
    }
}
