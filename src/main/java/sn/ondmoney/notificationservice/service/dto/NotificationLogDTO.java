package sn.ondmoney.notificationservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationChannel;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationStatus;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationType;

/**
 * A DTO for the {@link sn.ondmoney.notificationservice.domain.NotificationLog} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationLogDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    private Long notificationId;

    @NotNull
    private String userId;

    @NotNull
    private NotificationType type;

    @NotNull
    private NotificationChannel channel;

    @NotNull
    private NotificationStatus status;

    private String message;

    private String recipient;

    @NotNull
    private Instant timestamp;

    private Instant sentAt;

    private Integer retryCount;

    private String channelResults;

    private String action;

    private String details;

    private NotificationDTO notification;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getChannelResults() {
        return channelResults;
    }

    public void setChannelResults(String channelResults) {
        this.channelResults = channelResults;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public NotificationDTO getNotification() {
        return notification;
    }

    public void setNotification(NotificationDTO notification) {
        this.notification = notification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationLogDTO)) {
            return false;
        }

        NotificationLogDTO notificationLogDTO = (NotificationLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notificationLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationLogDTO{" +
            "id=" + getId() +
            ", notificationId=" + getNotificationId() +
            ", userId='" + getUserId() + "'" +
            ", type='" + getType() + "'" +
            ", channel='" + getChannel() + "'" +
            ", status='" + getStatus() + "'" +
            ", message='" + getMessage() + "'" +
            ", recipient='" + getRecipient() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", retryCount=" + getRetryCount() +
            ", channelResults='" + getChannelResults() + "'" +
            ", action='" + getAction() + "'" +
            ", details='" + getDetails() + "'" +
            ", notification=" + getNotification() +
            "}";
    }
}
