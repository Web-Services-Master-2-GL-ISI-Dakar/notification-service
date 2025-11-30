package sn.ondmoney.notificationservice.service.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO pour les alertes de sécurité
 */
public class SecurityEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventId;
    private String userId;
    private String eventType; // PASSWORD_RESET, SUSPICIOUS_LOGIN, ACCOUNT_LOCKED
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private String description;
    private String ipAddress;
    private String deviceInfo;
    private Instant timestamp;

    // Constructeurs
    public SecurityEventDTO() {}

    // Getters et Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return (
            "SecurityEventDTO{" +
            "eventId='" +
            eventId +
            '\'' +
            ", userId='" +
            userId +
            '\'' +
            ", eventType='" +
            eventType +
            '\'' +
            ", severity='" +
            severity +
            '\'' +
            '}'
        );
    }
}
