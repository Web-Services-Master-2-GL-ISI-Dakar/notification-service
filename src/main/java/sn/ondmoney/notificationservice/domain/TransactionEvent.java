package sn.ondmoney.notificationservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import sn.ondmoney.notificationservice.domain.enumeration.TransactionType;

/**
 * A TransactionEvent.
 */
@Entity
@Table(name = "transaction_event")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @NotNull
    @Column(name = "sender_account", nullable = false)
    private String senderAccount;

    @NotNull
    @Column(name = "receiver_account", nullable = false)
    private String receiverAccount;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionType type;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "additional_data")
    private String additionalData;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TransactionEvent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public TransactionEvent transactionId(String transactionId) {
        this.setTransactionId(transactionId);
        return this;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSenderAccount() {
        return this.senderAccount;
    }

    public TransactionEvent senderAccount(String senderAccount) {
        this.setSenderAccount(senderAccount);
        return this;
    }

    public void setSenderAccount(String senderAccount) {
        this.senderAccount = senderAccount;
    }

    public String getReceiverAccount() {
        return this.receiverAccount;
    }

    public TransactionEvent receiverAccount(String receiverAccount) {
        this.setReceiverAccount(receiverAccount);
        return this;
    }

    public void setReceiverAccount(String receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public TransactionEvent amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return this.type;
    }

    public TransactionEvent type(TransactionType type) {
        this.setType(type);
        return this;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public TransactionEvent timestamp(Instant timestamp) {
        this.setTimestamp(timestamp);
        return this;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getAdditionalData() {
        return this.additionalData;
    }

    public TransactionEvent additionalData(String additionalData) {
        this.setAdditionalData(additionalData);
        return this;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionEvent)) {
            return false;
        }
        return getId() != null && getId().equals(((TransactionEvent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionEvent{" +
            "id=" + getId() +
            ", transactionId='" + getTransactionId() + "'" +
            ", senderAccount='" + getSenderAccount() + "'" +
            ", receiverAccount='" + getReceiverAccount() + "'" +
            ", amount=" + getAmount() +
            ", type='" + getType() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", additionalData='" + getAdditionalData() + "'" +
            "}";
    }
}
