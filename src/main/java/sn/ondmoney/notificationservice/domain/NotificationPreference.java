package sn.ondmoney.notificationservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A NotificationPreference.
 */
@Entity
@Table(name = "notification_preference")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationPreference implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @NotNull
    @Column(name = "sms_enabled", nullable = false)
    private Boolean smsEnabled;

    @NotNull
    @Column(name = "email_enabled", nullable = false)
    private Boolean emailEnabled;

    @NotNull
    @Column(name = "push_enabled", nullable = false)
    private Boolean pushEnabled;

    @Column(name = "muted_types")
    private String mutedTypes;

    @NotNull
    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public NotificationPreference id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public NotificationPreference userId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getSmsEnabled() {
        return this.smsEnabled;
    }

    public NotificationPreference smsEnabled(Boolean smsEnabled) {
        this.setSmsEnabled(smsEnabled);
        return this;
    }

    public void setSmsEnabled(Boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public Boolean getEmailEnabled() {
        return this.emailEnabled;
    }

    public NotificationPreference emailEnabled(Boolean emailEnabled) {
        this.setEmailEnabled(emailEnabled);
        return this;
    }

    public void setEmailEnabled(Boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public Boolean getPushEnabled() {
        return this.pushEnabled;
    }

    public NotificationPreference pushEnabled(Boolean pushEnabled) {
        this.setPushEnabled(pushEnabled);
        return this;
    }

    public void setPushEnabled(Boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    public String getMutedTypes() {
        return this.mutedTypes;
    }

    public NotificationPreference mutedTypes(String mutedTypes) {
        this.setMutedTypes(mutedTypes);
        return this;
    }

    public void setMutedTypes(String mutedTypes) {
        this.mutedTypes = mutedTypes;
    }

    public String getLanguage() {
        return this.language;
    }

    public NotificationPreference language(String language) {
        this.setLanguage(language);
        return this;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public NotificationPreference updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationPreference)) {
            return false;
        }
        return getId() != null && getId().equals(((NotificationPreference) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationPreference{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", smsEnabled='" + getSmsEnabled() + "'" +
            ", emailEnabled='" + getEmailEnabled() + "'" +
            ", pushEnabled='" + getPushEnabled() + "'" +
            ", mutedTypes='" + getMutedTypes() + "'" +
            ", language='" + getLanguage() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
