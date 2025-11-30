package sn.ondmoney.notificationservice.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link sn.ondmoney.notificationservice.domain.NotificationPreference} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationPreferenceDTO implements Serializable {

    @NotNull
    private Long id;

    @NotNull
    private String userId;

    @NotNull
    private Boolean smsEnabled;

    @NotNull
    private Boolean emailEnabled;

    @NotNull
    private Boolean pushEnabled;

    private String mutedTypes;

    @NotNull
    private String language;

    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getSmsEnabled() {
        return smsEnabled;
    }

    public void setSmsEnabled(Boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public Boolean getEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(Boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public Boolean getPushEnabled() {
        return pushEnabled;
    }

    public void setPushEnabled(Boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    public String getMutedTypes() {
        return mutedTypes;
    }

    public void setMutedTypes(String mutedTypes) {
        this.mutedTypes = mutedTypes;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationPreferenceDTO)) {
            return false;
        }

        NotificationPreferenceDTO notificationPreferenceDTO = (NotificationPreferenceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notificationPreferenceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationPreferenceDTO{" +
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
