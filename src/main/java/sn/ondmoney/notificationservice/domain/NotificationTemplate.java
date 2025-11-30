package sn.ondmoney.notificationservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationType;

/**
 * A NotificationTemplate.
 */
@Entity
@Table(name = "notification_template")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "template_code", nullable = false, unique = true)
    private String templateCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @NotNull
    @Column(name = "language", nullable = false)
    private String language;

    @NotNull
    @Column(name = "subject", nullable = false)
    private String subject;

    @NotNull
    @Size(max = 5000)
    @Column(name = "body_template", length = 5000, nullable = false)
    private String bodyTemplate;

    @Size(max = 500)
    @Column(name = "sms_template", length = 500)
    private String smsTemplate;

    @Column(name = "push_title")
    private String pushTitle;

    @Column(name = "push_body")
    private String pushBody;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public NotificationTemplate id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
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

    public NotificationType getType() {
        return this.type;
    }

    public NotificationTemplate type(NotificationType type) {
        this.setType(type);
        return this;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getLanguage() {
        return this.language;
    }

    public NotificationTemplate language(String language) {
        this.setLanguage(language);
        return this;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSubject() {
        return this.subject;
    }

    public NotificationTemplate subject(String subject) {
        this.setSubject(subject);
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public String getSmsTemplate() {
        return this.smsTemplate;
    }

    public NotificationTemplate smsTemplate(String smsTemplate) {
        this.setSmsTemplate(smsTemplate);
        return this;
    }

    public void setSmsTemplate(String smsTemplate) {
        this.smsTemplate = smsTemplate;
    }

    public String getPushTitle() {
        return this.pushTitle;
    }

    public NotificationTemplate pushTitle(String pushTitle) {
        this.setPushTitle(pushTitle);
        return this;
    }

    public void setPushTitle(String pushTitle) {
        this.pushTitle = pushTitle;
    }

    public String getPushBody() {
        return this.pushBody;
    }

    public NotificationTemplate pushBody(String pushBody) {
        this.setPushBody(pushBody);
        return this;
    }

    public void setPushBody(String pushBody) {
        this.pushBody = pushBody;
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

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public NotificationTemplate createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public NotificationTemplate updatedAt(Instant updatedAt) {
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
            ", type='" + getType() + "'" +
            ", language='" + getLanguage() + "'" +
            ", subject='" + getSubject() + "'" +
            ", bodyTemplate='" + getBodyTemplate() + "'" +
            ", smsTemplate='" + getSmsTemplate() + "'" +
            ", pushTitle='" + getPushTitle() + "'" +
            ", pushBody='" + getPushBody() + "'" +
            ", active='" + getActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
