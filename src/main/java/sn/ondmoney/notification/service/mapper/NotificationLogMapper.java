package sn.ondmoney.notification.service.mapper;

import org.mapstruct.*;
import sn.ondmoney.notification.domain.NotificationLog;
import sn.ondmoney.notification.domain.NotificationTemplate;
import sn.ondmoney.notification.service.dto.*;

/**
 * Mapper for the entity {@link NotificationLog} and its DTO {@link NotificationLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationLogMapper extends EntityMapper<NotificationLogDTO, NotificationLog> {
    @Mapping(target = "notificationTemplateUsed", source = "notificationTemplateUsed", qualifiedByName = "notificationTemplateId")
    NotificationLogDTO toDto(NotificationLog s);

    @Named("notificationTemplateId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NotificationTemplateDTO toDtoNotificationTemplateId(NotificationTemplate notificationTemplate);

    @Mappings(
        {
            // Champ nécessitant une conversion de DTO à String (JSON/TextBlob)
            @Mapping(target = "payload", source = "request.payload", qualifiedByName = "serializePayload"),
            // Champs de Log qui ne peuvent pas être tirés directement de la Request et doivent être ignorés:
            // Ils seront fixés par la logique métier (e.g., dans le service)
            @Mapping(target = "id", ignore = true), // ID généré
            @Mapping(target = "userId", ignore = true), // Déduit du payload par le service
            @Mapping(target = "recipient", ignore = true), // Déduit du payload par le service
            @Mapping(target = "notificationChannel", ignore = true), // Fixé par le service (la Request contient une liste)
            @Mapping(target = "notificationStatus", ignore = true), // Initialisé à PENDING par le service
            @Mapping(target = "sentAt", ignore = true), // Date d'envoi non connue à la création
            @Mapping(target = "externalEventRef", ignore = true),
            @Mapping(target = "errorMessage", ignore = true),
            @Mapping(target = "retryCount", ignore = true),
            @Mapping(target = "failedAt", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "notificationTemplateUsed", ignore = true), // Fixé par le service après recherche
        }
    )
    NotificationLogDTO fromNotificationRequest(NotificationRequest request);

    // --- 3. NotificationLogDTO -> DeliveryReceipt (Mise à jour du Log) ---

    @Mappings(
        {
            // Renommage de l'ID du log en notificationLogId dans le reçu
            @Mapping(target = "notificationLogId", source = "id"),
            // Mappages directs de DTO à Receipt
            @Mapping(target = "eventRef", source = "eventRef"),
            @Mapping(target = "eventTime", source = "eventTime"),
            @Mapping(target = "recipient", source = "recipient"),
            @Mapping(target = "notificationType", source = "notificationType"),
            @Mapping(target = "notificationChannel", source = "notificationChannel"),
            @Mapping(target = "notificationStatus", source = "notificationStatus"),
            @Mapping(target = "sentAt", source = "sentAt"),
            @Mapping(target = "errorMessage", source = "errorMessage"),
            // Les autres champs de NotificationLogDTO sont absents de DeliveryReceipt et sont ignorés
        }
    )
    DeliveryReceipt toDeliveryReceipt(NotificationLogDTO notificationLog);

    // --- Custom Mapping pour la sérialisation du Payload ---
    /**
     * Convertit le DTO NotificationPayload en chaîne de caractères pour le champ payload TextBlob.
     * Cette méthode doit idéalement sérialiser en JSON (via Jackson ObjectMapper).
     *
     * @param payload Le DTO NotificationPayload à sérialiser.
     * @return La représentation String/JSON du payload.
     */
    @Named("serializePayload")
    default String serializePayload(NotificationPayload payload) {
        if (payload == null) {
            return null;
        }
        // NOTE: En production, il faudrait utiliser un ObjectMapper (Jackson) injecté ou inclus
        // dans un composant MapStruct 'uses' pour garantir une sérialisation JSON robuste.
        return payload.toString(); // Utilisation de la méthode toString() de Lombok pour l'exemple
    }
}
