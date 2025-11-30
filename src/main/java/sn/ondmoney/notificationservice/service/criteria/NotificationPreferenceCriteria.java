package sn.ondmoney.notificationservice.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link sn.ondmoney.notificationservice.domain.NotificationPreference} entity. This class is used
 * in {@link sn.ondmoney.notificationservice.web.rest.NotificationPreferenceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /notification-preferences?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationPreferenceCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter userId;

    private BooleanFilter smsEnabled;

    private BooleanFilter emailEnabled;

    private BooleanFilter pushEnabled;

    private StringFilter mutedTypes;

    private StringFilter language;

    private InstantFilter updatedAt;

    private Boolean distinct;

    public NotificationPreferenceCriteria() {}

    public NotificationPreferenceCriteria(NotificationPreferenceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(StringFilter::copy).orElse(null);
        this.smsEnabled = other.optionalSmsEnabled().map(BooleanFilter::copy).orElse(null);
        this.emailEnabled = other.optionalEmailEnabled().map(BooleanFilter::copy).orElse(null);
        this.pushEnabled = other.optionalPushEnabled().map(BooleanFilter::copy).orElse(null);
        this.mutedTypes = other.optionalMutedTypes().map(StringFilter::copy).orElse(null);
        this.language = other.optionalLanguage().map(StringFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public NotificationPreferenceCriteria copy() {
        return new NotificationPreferenceCriteria(this);
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

    public BooleanFilter getSmsEnabled() {
        return smsEnabled;
    }

    public Optional<BooleanFilter> optionalSmsEnabled() {
        return Optional.ofNullable(smsEnabled);
    }

    public BooleanFilter smsEnabled() {
        if (smsEnabled == null) {
            setSmsEnabled(new BooleanFilter());
        }
        return smsEnabled;
    }

    public void setSmsEnabled(BooleanFilter smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public BooleanFilter getEmailEnabled() {
        return emailEnabled;
    }

    public Optional<BooleanFilter> optionalEmailEnabled() {
        return Optional.ofNullable(emailEnabled);
    }

    public BooleanFilter emailEnabled() {
        if (emailEnabled == null) {
            setEmailEnabled(new BooleanFilter());
        }
        return emailEnabled;
    }

    public void setEmailEnabled(BooleanFilter emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public BooleanFilter getPushEnabled() {
        return pushEnabled;
    }

    public Optional<BooleanFilter> optionalPushEnabled() {
        return Optional.ofNullable(pushEnabled);
    }

    public BooleanFilter pushEnabled() {
        if (pushEnabled == null) {
            setPushEnabled(new BooleanFilter());
        }
        return pushEnabled;
    }

    public void setPushEnabled(BooleanFilter pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    public StringFilter getMutedTypes() {
        return mutedTypes;
    }

    public Optional<StringFilter> optionalMutedTypes() {
        return Optional.ofNullable(mutedTypes);
    }

    public StringFilter mutedTypes() {
        if (mutedTypes == null) {
            setMutedTypes(new StringFilter());
        }
        return mutedTypes;
    }

    public void setMutedTypes(StringFilter mutedTypes) {
        this.mutedTypes = mutedTypes;
    }

    public StringFilter getLanguage() {
        return language;
    }

    public Optional<StringFilter> optionalLanguage() {
        return Optional.ofNullable(language);
    }

    public StringFilter language() {
        if (language == null) {
            setLanguage(new StringFilter());
        }
        return language;
    }

    public void setLanguage(StringFilter language) {
        this.language = language;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
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
        final NotificationPreferenceCriteria that = (NotificationPreferenceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(smsEnabled, that.smsEnabled) &&
            Objects.equals(emailEnabled, that.emailEnabled) &&
            Objects.equals(pushEnabled, that.pushEnabled) &&
            Objects.equals(mutedTypes, that.mutedTypes) &&
            Objects.equals(language, that.language) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, smsEnabled, emailEnabled, pushEnabled, mutedTypes, language, updatedAt, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationPreferenceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalSmsEnabled().map(f -> "smsEnabled=" + f + ", ").orElse("") +
            optionalEmailEnabled().map(f -> "emailEnabled=" + f + ", ").orElse("") +
            optionalPushEnabled().map(f -> "pushEnabled=" + f + ", ").orElse("") +
            optionalMutedTypes().map(f -> "mutedTypes=" + f + ", ").orElse("") +
            optionalLanguage().map(f -> "language=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
