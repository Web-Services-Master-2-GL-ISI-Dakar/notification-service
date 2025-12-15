package sn.ondmoney.notification.service.dto;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sn.ondmoney.notification.domain.enumeration.NotificationStatus;
import sn.ondmoney.notification.domain.enumeration.NotificationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {

    // --- Corrélation ---
    private String eventRef; // L'ID unique de l'événement (du TXE/AUTH)
    private Instant eventTime;

    // --- Statut Résumé ---
    private NotificationStatus notificationStatus;
    private NotificationType notificationType;

    // --- Détails de la Livraison ---
    private List<DeliveryReceipt> deliveryReceipts; // Les résultats détaillés par canal

    // --- Payload Original ---
    // Le payload original est inclus pour que le consommateur puisse vérifier les montants/parties.
    private String payload;
}
