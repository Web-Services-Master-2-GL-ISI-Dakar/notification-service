package sn.ondmoney.notificationservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationChannel;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationStatus;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationType;

/**
 * A NotificationLog.
 */
@Entity
@Table(name = "notification_log")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private String userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationChannel channel;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status;

    @Column(name = "message")
    private String message;

    @Column(name = "recipient")
    private String recipient;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "channel_results")
    private String channelResults;

    @Column(name = "action")
    private String action;

    @Column(name = "details")
    private String details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "notificationLogs" }, allowSetters = true)
    private Notification notification;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public NotificationLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNotificationId() {
        return this.notificationId;
    }

    public NotificationLog notificationId(Long notificationId) {
        this.setNotificationId(notificationId);
        return this;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
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

    public NotificationType getType() {
        return this.type;
    }

    public NotificationLog type(NotificationType type) {
        this.setType(type);
        return this;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationChannel getChannel() {
        return this.channel;
    }

    public NotificationLog channel(NotificationChannel channel) {
        this.setChannel(channel);
        return this;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public NotificationStatus getStatus() {
        return this.status;
    }

    public NotificationLog status(NotificationStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return this.message;
    }

    public NotificationLog message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public NotificationLog timestamp(Instant timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
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

    public String getChannelResults() {
        return this.channelResults;
    }

    public NotificationLog channelResults(String channelResults) {
        this.setChannelResults(channelResults);
        return this;
    }

    public void setChannelResults(String channelResults) {
        this.channelResults = channelResults;
    }

    public String getAction() {
        return this.action;
    }

    public NotificationLog action(String action) {
        this.setAction(action);
        return this;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return this.details;
    }

    public NotificationLog details(String details) {
        this.setDetails(details);
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Notification getNotification() {
        return this.notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public NotificationLog notification(Notification notification) {
        this.setNotification(notification);
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
            "}";
    }
}
