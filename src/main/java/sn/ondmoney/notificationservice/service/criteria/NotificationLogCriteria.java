package sn.ondmoney.notificationservice.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationChannel;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationStatus;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationType;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.ondmoney.notificationservice.domain.NotificationLog} entity.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationLogCriteria implements Serializable, Criteria {

    /**
     * Class for filtering NotificationType
     */
    public static class NotificationTypeFilter extends Filter<NotificationType> {

        public NotificationTypeFilter() {}

        public NotificationTypeFilter(NotificationTypeFilter filter) {
            super(filter);
        }

        @Override
        public NotificationTypeFilter copy() {
            return new NotificationTypeFilter(this);
        }
    }

    /**
     * Class for filtering NotificationChannel
     */
    public static class NotificationChannelFilter extends Filter<NotificationChannel> {

        public NotificationChannelFilter() {}

        public NotificationChannelFilter(NotificationChannelFilter filter) {
            super(filter);
        }

        @Override
        public NotificationChannelFilter copy() {
            return new NotificationChannelFilter(this);
        }
    }

    /**
     * Class for filtering NotificationStatus
     */
    public static class NotificationStatusFilter extends Filter<NotificationStatus> {

        public NotificationStatusFilter() {}

        public NotificationStatusFilter(NotificationStatusFilter filter) {
            super(filter);
        }

        @Override
        public NotificationStatusFilter copy() {
            return new NotificationStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    // CORRECTION : Un seul notificationId (pour la relation avec Notification)
    private LongFilter notificationId;

    private StringFilter userId;

    private NotificationTypeFilter type;

    private NotificationChannelFilter channel;

    private NotificationStatusFilter status;

    private StringFilter message;

    private StringFilter recipient;

    private InstantFilter timestamp;

    private InstantFilter sentAt;

    private IntegerFilter retryCount;

    private StringFilter channelResults;

    private StringFilter action;

    private StringFilter details;

    private Boolean distinct;

    public NotificationLogCriteria() {}

    public NotificationLogCriteria(NotificationLogCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.notificationId = other.optionalNotificationId().map(LongFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(StringFilter::copy).orElse(null);
        this.type = other.optionalType().map(NotificationTypeFilter::copy).orElse(null);
        this.channel = other.optionalChannel().map(NotificationChannelFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(NotificationStatusFilter::copy).orElse(null);
        this.message = other.optionalMessage().map(StringFilter::copy).orElse(null);
        this.recipient = other.optionalRecipient().map(StringFilter::copy).orElse(null);
        this.timestamp = other.optionalTimestamp().map(InstantFilter::copy).orElse(null);
        this.sentAt = other.optionalSentAt().map(InstantFilter::copy).orElse(null);
        this.retryCount = other.optionalRetryCount().map(IntegerFilter::copy).orElse(null);
        this.channelResults = other.optionalChannelResults().map(StringFilter::copy).orElse(null);
        this.action = other.optionalAction().map(StringFilter::copy).orElse(null);
        this.details = other.optionalDetails().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public NotificationLogCriteria copy() {
        return new NotificationLogCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getNotificationId() {
        return notificationId;
    }

    public Optional<LongFilter> optionalNotificationId() {
        return Optional.ofNullable(notificationId);
    }

    public LongFilter notificationId() {
        if (notificationId == null) {
            setNotificationId(new LongFilter());
        }
        return notificationId;
    }

    public void setNotificationId(LongFilter notificationId) {
        this.notificationId = notificationId;
    }

    public StringFilter getUserId() {
        return userId;
    }

    public Optional<StringFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public StringFilter userId() {
        if (userId == null) {
            setUserId(new StringFilter());
        }
        return userId;
    }

    public void setUserId(StringFilter userId) {
        this.userId = userId;
    }

    public NotificationTypeFilter getType() {
        return type;
    }

    public Optional<NotificationTypeFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public NotificationTypeFilter type() {
        if (type == null) {
            setType(new NotificationTypeFilter());
        }
        return type;
    }

    public void setType(NotificationTypeFilter type) {
        this.type = type;
    }

    public NotificationChannelFilter getChannel() {
        return channel;
    }

    public Optional<NotificationChannelFilter> optionalChannel() {
        return Optional.ofNullable(channel);
    }

    public NotificationChannelFilter channel() {
        if (channel == null) {
            setChannel(new NotificationChannelFilter());
        }
        return channel;
    }

    public void setChannel(NotificationChannelFilter channel) {
        this.channel = channel;
    }

    public NotificationStatusFilter getStatus() {
        return status;
    }

    public Optional<NotificationStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public NotificationStatusFilter status() {
        if (status == null) {
            setStatus(new NotificationStatusFilter());
        }
        return status;
    }

    public void setStatus(NotificationStatusFilter status) {
        this.status = status;
    }

    public StringFilter getMessage() {
        return message;
    }

    public Optional<StringFilter> optionalMessage() {
        return Optional.ofNullable(message);
    }

    public StringFilter message() {
        if (message == null) {
            setMessage(new StringFilter());
        }
        return message;
    }

    public void setMessage(StringFilter message) {
        this.message = message;
    }

    public StringFilter getRecipient() {
        return recipient;
    }

    public Optional<StringFilter> optionalRecipient() {
        return Optional.ofNullable(recipient);
    }

    public StringFilter recipient() {
        if (recipient == null) {
            setRecipient(new StringFilter());
        }
        return recipient;
    }

    public void setRecipient(StringFilter recipient) {
        this.recipient = recipient;
    }

    public InstantFilter getTimestamp() {
        return timestamp;
    }

    public Optional<InstantFilter> optionalTimestamp() {
        return Optional.ofNullable(timestamp);
    }

    public InstantFilter timestamp() {
        if (timestamp == null) {
            setTimestamp(new InstantFilter());
        }
        return timestamp;
    }

    public void setTimestamp(InstantFilter timestamp) {
        this.timestamp = timestamp;
    }

    public InstantFilter getSentAt() {
        return sentAt;
    }

    public Optional<InstantFilter> optionalSentAt() {
        return Optional.ofNullable(sentAt);
    }

    public InstantFilter sentAt() {
        if (sentAt == null) {
            setSentAt(new InstantFilter());
        }
        return sentAt;
    }

    public void setSentAt(InstantFilter sentAt) {
        this.sentAt = sentAt;
    }

    public IntegerFilter getRetryCount() {
        return retryCount;
    }

    public Optional<IntegerFilter> optionalRetryCount() {
        return Optional.ofNullable(retryCount);
    }

    public IntegerFilter retryCount() {
        if (retryCount == null) {
            setRetryCount(new IntegerFilter());
        }
        return retryCount;
    }

    public void setRetryCount(IntegerFilter retryCount) {
        this.retryCount = retryCount;
    }

    public StringFilter getChannelResults() {
        return channelResults;
    }

    public Optional<StringFilter> optionalChannelResults() {
        return Optional.ofNullable(channelResults);
    }

    public StringFilter channelResults() {
        if (channelResults == null) {
            setChannelResults(new StringFilter());
        }
        return channelResults;
    }

    public void setChannelResults(StringFilter channelResults) {
        this.channelResults = channelResults;
    }

    public StringFilter getAction() {
        return action;
    }

    public Optional<StringFilter> optionalAction() {
        return Optional.ofNullable(action);
    }

    public StringFilter action() {
        if (action == null) {
            setAction(new StringFilter());
        }
        return action;
    }

    public void setAction(StringFilter action) {
        this.action = action;
    }

    public StringFilter getDetails() {
        return details;
    }

    public Optional<StringFilter> optionalDetails() {
        return Optional.ofNullable(details);
    }

    public StringFilter details() {
        if (details == null) {
            setDetails(new StringFilter());
        }
        return details;
    }

    public void setDetails(StringFilter details) {
        this.details = details;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NotificationLogCriteria that = (NotificationLogCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(notificationId, that.notificationId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(type, that.type) &&
            Objects.equals(channel, that.channel) &&
            Objects.equals(status, that.status) &&
            Objects.equals(message, that.message) &&
            Objects.equals(recipient, that.recipient) &&
            Objects.equals(timestamp, that.timestamp) &&
            Objects.equals(sentAt, that.sentAt) &&
            Objects.equals(retryCount, that.retryCount) &&
            Objects.equals(channelResults, that.channelResults) &&
            Objects.equals(action, that.action) &&
            Objects.equals(details, that.details) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            notificationId,
            userId,
            type,
            channel,
            status,
            message,
            recipient,
            timestamp,
            sentAt,
            retryCount,
            channelResults,
            action,
            details,
            distinct
        );
    }

    @Override
    public String toString() {
        return (
            "NotificationLogCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNotificationId().map(f -> "notificationId=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalChannel().map(f -> "channel=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalMessage().map(f -> "message=" + f + ", ").orElse("") +
            optionalRecipient().map(f -> "recipient=" + f + ", ").orElse("") +
            optionalTimestamp().map(f -> "timestamp=" + f + ", ").orElse("") +
            optionalSentAt().map(f -> "sentAt=" + f + ", ").orElse("") +
            optionalRetryCount().map(f -> "retryCount=" + f + ", ").orElse("") +
            optionalChannelResults().map(f -> "channelResults=" + f + ", ").orElse("") +
            optionalAction().map(f -> "action=" + f + ", ").orElse("") +
            optionalDetails().map(f -> "details=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
            "}"
        );
    }
}
