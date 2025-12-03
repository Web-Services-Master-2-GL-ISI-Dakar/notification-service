package sn.ondmoney.notification.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import sn.ondmoney.notification.domain.enumeration.NotificationChannel;
import sn.ondmoney.notification.domain.enumeration.NotificationStatus;
import sn.ondmoney.notification.domain.enumeration.NotificationType;

/**
 * A DTO for the {@link sn.ondmoney.notification.domain.NotificationLog} entity.
 */
@Schema(description = "NotificationLog: Log d'envoi de notification uniforme (Audit)")
@SuppressWarnings("common-java:DuplicatedBlocks")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationLogDTO implements Serializable {

    private String id;

    @NotNull
    private String eventRef;

    private Instant eventTime;

    private String userId;

    @NotNull
    private String recipient;

    @NotNull
    private NotificationType notificationType;

    @NotNull
    private NotificationStatus notificationStatus;

    @NotNull
    private NotificationChannel notificationChannel;

    private String payload;

    @NotNull
    private Instant sentAt;

    private String externalEventRef;

    @Size(max = 2048)
    private String errorMessage;

    @Min(value = 0)
    private Integer retryCount;

    private Instant failedAt;

    private Instant createdAt;

    private Instant updatedAt;

    private NotificationTemplateDTO notificationTemplateUsed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventRef() {
        return eventRef;
    }

    public void setEventRef(String eventRef) {
        this.eventRef = eventRef;
    }

    public Instant getEventTime() {
        return eventTime;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public NotificationChannel getNotificationChannel() {
        return notificationChannel;
    }

    public void setNotificationChannel(NotificationChannel notificationChannel) {
        this.notificationChannel = notificationChannel;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public String getExternalEventRef() {
        return externalEventRef;
    }

    public void setExternalEventRef(String externalEventRef) {
        this.externalEventRef = externalEventRef;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Instant getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(Instant failedAt) {
        this.failedAt = failedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public NotificationTemplateDTO getNotificationTemplateUsed() {
        return notificationTemplateUsed;
    }

    public void setNotificationTemplateUsed(NotificationTemplateDTO notificationTemplateUsed) {
        this.notificationTemplateUsed = notificationTemplateUsed;
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
            "id='" + getId() + "'" +
            ", eventRef='" + getEventRef() + "'" +
            ", eventTime='" + getEventTime() + "'" +
            ", userId='" + getUserId() + "'" +
            ", recipient='" + getRecipient() + "'" +
            ", notificationType='" + getNotificationType() + "'" +
            ", notificationStatus='" + getNotificationStatus() + "'" +
            ", notificationChannel='" + getNotificationChannel() + "'" +
            ", payload='" + getPayload() + "'" +
            ", sentAt='" + getSentAt() + "'" +
            ", externalEventRef='" + getExternalEventRef() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", retryCount=" + getRetryCount() +
            ", failedAt='" + getFailedAt() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", notificationTemplateUsed=" + getNotificationTemplateUsed() +
            "}";
    }
}
