package sn.ondmoney.notificationservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationType;
import sn.ondmoney.notificationservice.domain.enumeration.Priority;

/**
 * A NotificationRequest.
 */
@Entity
@Table(name = "notification_request")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "account_number")
    private String accountNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @NotNull
    @Column(name = "channels", nullable = false)
    private String channels;

    @Column(name = "data")
    private String data;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @NotNull
    @Column(name = "immediate", nullable = false)
    private Boolean immediate;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public NotificationRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public NotificationRequest userId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public NotificationRequest accountNumber(String accountNumber) {
        this.setAccountNumber(accountNumber);
        return this;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public NotificationType getType() {
        return this.type;
    }

    public NotificationRequest type(NotificationType type) {
        this.setType(type);
        return this;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getChannels() {
        return this.channels;
    }

    public NotificationRequest channels(String channels) {
        this.setChannels(channels);
        return this;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }

    public String getData() {
        return this.data;
    }

    public NotificationRequest data(String data) {
        this.setData(data);
        return this;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public NotificationRequest priority(Priority priority) {
        this.setPriority(priority);
        return this;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Boolean getImmediate() {
        return this.immediate;
    }

    public NotificationRequest immediate(Boolean immediate) {
        this.setImmediate(immediate);
        return this;
    }

    public void setImmediate(Boolean immediate) {
        this.immediate = immediate;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationRequest)) {
            return false;
        }
        return getId() != null && getId().equals(((NotificationRequest) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationRequest{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", accountNumber='" + getAccountNumber() + "'" +
            ", type='" + getType() + "'" +
            ", channels='" + getChannels() + "'" +
            ", data='" + getData() + "'" +
            ", priority='" + getPriority() + "'" +
            ", immediate='" + getImmediate() + "'" +
            "}";
    }
}
