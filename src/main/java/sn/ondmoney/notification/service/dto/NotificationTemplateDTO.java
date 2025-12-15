package sn.ondmoney.notification.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import sn.ondmoney.notification.domain.enumeration.NotificationChannel;
import sn.ondmoney.notification.domain.enumeration.NotificationLanguage;
import sn.ondmoney.notification.domain.enumeration.NotificationTemplateType;
import sn.ondmoney.notification.domain.enumeration.NotificationType;

/**
 * A DTO for the {@link sn.ondmoney.notification.domain.NotificationTemplate} entity.
 */
@Schema(description = "NotificationTemplate: Modèle utilisé pour générer le message final.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationTemplateDTO implements Serializable {

    private String id;

    @NotNull
    private String templateCode;

    @NotNull
    private NotificationChannel notificationChannel;

    @NotNull
    private NotificationLanguage notificationLanguage;

    @NotNull
    private NotificationType notificationType;

    @NotNull
    private NotificationTemplateType notificationTemplateType;

    @Size(max = 255)
    private String subjectTemplate;

    private String bodyTemplate;

    @NotNull
    private Boolean active;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public NotificationChannel getNotificationChannel() {
        return notificationChannel;
    }

    public void setNotificationChannel(NotificationChannel notificationChannel) {
        this.notificationChannel = notificationChannel;
    }

    public NotificationLanguage getNotificationLanguage() {
        return notificationLanguage;
    }

    public void setNotificationLanguage(NotificationLanguage notificationLanguage) {
        this.notificationLanguage = notificationLanguage;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationTemplateType getNotificationTemplateType() {
        return notificationTemplateType;
    }

    public void setNotificationTemplateType(NotificationTemplateType notificationTemplateType) {
        this.notificationTemplateType = notificationTemplateType;
    }

    public String getSubjectTemplate() {
        return subjectTemplate;
    }

    public void setSubjectTemplate(String subjectTemplate) {
        this.subjectTemplate = subjectTemplate;
    }

    public String getBodyTemplate() {
        return bodyTemplate;
    }

    public void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationTemplateDTO)) {
            return false;
        }

        NotificationTemplateDTO notificationTemplateDTO = (NotificationTemplateDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notificationTemplateDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationTemplateDTO{" +
            "id='" + getId() + "'" +
            ", templateCode='" + getTemplateCode() + "'" +
            ", notificationChannel='" + getNotificationChannel() + "'" +
            ", notificationLanguage='" + getNotificationLanguage() + "'" +
            ", notificationType='" + getNotificationType() + "'" +
            ", notificationTemplateType='" + getNotificationTemplateType() + "'" +
            ", subjectTemplate='" + getSubjectTemplate() + "'" +
            ", bodyTemplate='" + getBodyTemplate() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}
