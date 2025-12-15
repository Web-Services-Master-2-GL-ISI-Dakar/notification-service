package sn.ondmoney.notification.service.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.ondmoney.notification.domain.enumeration.NotificationChannel;
import sn.ondmoney.notification.domain.enumeration.NotificationStatus;
import sn.ondmoney.notification.domain.enumeration.NotificationType;

/**
 * DTO représentant le reçu de livraison retourné par un Handler après une tentative d'envoi.
 * Utilisé pour mettre à jour l'audit (NotificationLog).
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DeliveryReceipt {

    private String notificationLogId;
    private String eventRef;
    private Instant eventTime;
    private String recipient;
    private NotificationType notificationType;
    private NotificationChannel notificationChannel;
    private NotificationStatus notificationStatus;
    private Instant sentAt;
    private String errorMessage;
}
