package sn.ondmoney.notification.domain;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import sn.ondmoney.notification.domain.enumeration.NotificationChannel;
import sn.ondmoney.notification.domain.enumeration.NotificationStatus;
import sn.ondmoney.notification.domain.enumeration.NotificationType;

/**
 * NotificationLog: Log d'envoi de notification uniforme (Audit)
 */
@Document(collection = "notification_log")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "notificationlog")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("event_ref")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String eventRef;

    @Field("event_time")
    private Instant eventTime;

    @Field("user_id")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String userId;

    @NotNull
    @Field("recipient")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String recipient;

    @NotNull
    @Field("notification_type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private NotificationType notificationType;

    @NotNull
    @Field("notification_status")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private NotificationStatus notificationStatus;

    @NotNull
    @Field("notification_channel")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private NotificationChannel notificationChannel;

    @Field("payload")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String payload;

    @NotNull
    @Field("sent_at")
    private Instant sentAt;

    @Field("external_event_ref")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String externalEventRef;

    @Size(max = 2048)
    @Field("error_message")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String errorMessage;

    @Min(value = 0)
    @Field("retry_count")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer retryCount;

    @Field("failed_at")
    private Instant failedAt;

    @Field("created_at")
    private Instant createdAt;

    @Field("updated_at")
    private Instant updatedAt;

    @DBRef
    @Field("notificationTemplateUsed")
    private NotificationTemplate notificationTemplateUsed;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public NotificationLog id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventRef() {
        return this.eventRef;
    }

    public NotificationLog eventRef(String eventRef) {
        this.setEventRef(eventRef);
        return this;
    }

    public void setEventRef(String eventRef) {
        this.eventRef = eventRef;
    }

    public Instant getEventTime() {
        return this.eventTime;
    }

    public NotificationLog eventTime(Instant eventTime) {
        this.setEventTime(eventTime);
        return this;
    }

    public void setEventTime(Instant eventTime) {
        this.eventTime = eventTime;
    }

    public String getUserId() {
        return this.userId;
    }

    public NotificationLog userId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public NotificationLog recipient(String recipient) {
        this.setRecipient(recipient);
        return this;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public NotificationType getNotificationType() {
        return this.notificationType;
    }

    public NotificationLog notificationType(NotificationType notificationType) {
        this.setNotificationType(notificationType);
        return this;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationStatus getNotificationStatus() {
        return this.notificationStatus;
    }

    public NotificationLog notificationStatus(NotificationStatus notificationStatus) {
        this.setNotificationStatus(notificationStatus);
        return this;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public NotificationChannel getNotificationChannel() {
        return this.notificationChannel;
    }

    public NotificationLog notificationChannel(NotificationChannel notificationChannel) {
        this.setNotificationChannel(notificationChannel);
        return this;
    }

    public void setNotificationChannel(NotificationChannel notificationChannel) {
        this.notificationChannel = notificationChannel;
    }

    public String getPayload() {
        return this.payload;
    }

    public NotificationLog payload(String payload) {
        this.setPayload(payload);
        return this;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Instant getSentAt() {
        return this.sentAt;
    }

    public NotificationLog sentAt(Instant sentAt) {
        this.setSentAt(sentAt);
        return this;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public String getExternalEventRef() {
        return this.externalEventRef;
    }

    public NotificationLog externalEventRef(String externalEventRef) {
        this.setExternalEventRef(externalEventRef);
        return this;
    }

    public void setExternalEventRef(String externalEventRef) {
        this.externalEventRef = externalEventRef;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public NotificationLog errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getRetryCount() {
        return this.retryCount;
    }

    public NotificationLog retryCount(Integer retryCount) {
        this.setRetryCount(retryCount);
        return this;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Instant getFailedAt() {
        return this.failedAt;
    }

    public NotificationLog failedAt(Instant failedAt) {
        this.setFailedAt(failedAt);
        return this;
    }

    public void setFailedAt(Instant failedAt) {
        this.failedAt = failedAt;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public NotificationLog createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public NotificationLog updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public NotificationTemplate getNotificationTemplateUsed() {
        return this.notificationTemplateUsed;
    }

    public void setNotificationTemplateUsed(NotificationTemplate notificationTemplate) {
        this.notificationTemplateUsed = notificationTemplate;
    }

    public NotificationLog notificationTemplateUsed(NotificationTemplate notificationTemplate) {
        this.setNotificationTemplateUsed(notificationTemplate);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationLog)) {
            return false;
        }
        return getId() != null && getId().equals(((NotificationLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationLog{" +
            "id=" + getId() +
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
            "}";
    }
}
