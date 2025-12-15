package sn.ondmoney.notification.domain;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import sn.ondmoney.notification.domain.enumeration.NotificationChannel;
import sn.ondmoney.notification.domain.enumeration.NotificationLanguage;
import sn.ondmoney.notification.domain.enumeration.NotificationTemplateType;
import sn.ondmoney.notification.domain.enumeration.NotificationType;

/**
 * NotificationTemplate: Modèle utilisé pour générer le message final.
 */
@Document(collection = "notification_template")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "notificationtemplate")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("template_code")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String templateCode;

    @NotNull
    @Field("notification_channel")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private NotificationChannel notificationChannel;

    @NotNull
    @Field("notification_language")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private NotificationLanguage notificationLanguage;

    @NotNull
    @Field("notification_type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private NotificationType notificationType;

    @NotNull
    @Field("notification_template_type")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Keyword)
    private NotificationTemplateType notificationTemplateType;

    @Size(max = 255)
    @Field("subject_template")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String subjectTemplate;

    @Field("body_template")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String bodyTemplate;

    @NotNull
    @Field("active")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean active;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public NotificationTemplate id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplateCode() {
        return this.templateCode;
    }

    public NotificationTemplate templateCode(String templateCode) {
        this.setTemplateCode(templateCode);
        return this;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public NotificationChannel getNotificationChannel() {
        return this.notificationChannel;
    }

    public NotificationTemplate notificationChannel(NotificationChannel notificationChannel) {
        this.setNotificationChannel(notificationChannel);
        return this;
    }

    public void setNotificationChannel(NotificationChannel notificationChannel) {
        this.notificationChannel = notificationChannel;
    }

    public NotificationLanguage getNotificationLanguage() {
        return this.notificationLanguage;
    }

    public NotificationTemplate notificationLanguage(NotificationLanguage notificationLanguage) {
        this.setNotificationLanguage(notificationLanguage);
        return this;
    }

    public void setNotificationLanguage(NotificationLanguage notificationLanguage) {
        this.notificationLanguage = notificationLanguage;
    }

    public NotificationType getNotificationType() {
        return this.notificationType;
    }

    public NotificationTemplate notificationType(NotificationType notificationType) {
        this.setNotificationType(notificationType);
        return this;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationTemplateType getNotificationTemplateType() {
        return this.notificationTemplateType;
    }

    public NotificationTemplate notificationTemplateType(NotificationTemplateType notificationTemplateType) {
        this.setNotificationTemplateType(notificationTemplateType);
        return this;
    }

    public void setNotificationTemplateType(NotificationTemplateType notificationTemplateType) {
        this.notificationTemplateType = notificationTemplateType;
    }

    public String getSubjectTemplate() {
        return this.subjectTemplate;
    }

    public NotificationTemplate subjectTemplate(String subjectTemplate) {
        this.setSubjectTemplate(subjectTemplate);
        return this;
    }

    public void setSubjectTemplate(String subjectTemplate) {
        this.subjectTemplate = subjectTemplate;
    }

    public String getBodyTemplate() {
        return this.bodyTemplate;
    }

    public NotificationTemplate bodyTemplate(String bodyTemplate) {
        this.setBodyTemplate(bodyTemplate);
        return this;
    }

    public void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }

    public Boolean getActive() {
        return this.active;
    }

    public NotificationTemplate active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationTemplate)) {
            return false;
        }
        return getId() != null && getId().equals(((NotificationTemplate) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationTemplate{" +
            "id=" + getId() +
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
