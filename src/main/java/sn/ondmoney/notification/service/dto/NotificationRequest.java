package sn.ondmoney.notification.service.dto;

import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import sn.ondmoney.notification.domain.enumeration.NotificationChannel;
import sn.ondmoney.notification.domain.enumeration.NotificationType;

/**
 * DTO interne représentant la demande de notification.
 * Contient les identifiants pour le ciblage et les données brutes pour le templating.
 */
@Getter
@Builder
public class NotificationRequest {

    /// ID de la transaction/événement source (TXE/AUTH)
    private final String eventRef;
    /// Date/heure de dépôt dans le Kafka ou reçu du microservice NOTIF si appel direct (endpoint SOAP)
    private final Instant eventTime;

    // --- Routing et Type ---
    private final NotificationType notificationType; // Ex: OTP_SENT, TRANSFER_SENT_COMPLETED
    private final List<NotificationChannel> notificationChannels; // Canaux demandés par le service émetteur
    private final NotificationChannel notificationChannel; // Canaux demandés par le service émetteur

    // --- Données pour le Templating (Slots à Remplir) ---
    private final NotificationPayload payload; // DTO générique des données (montant, solde, code OTP)
}
