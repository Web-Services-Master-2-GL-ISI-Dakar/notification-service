package sn.ondmoney.notificationservice.soap;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import sn.ondmoney.notification.schemas.*;
import sn.ondmoney.notificationservice.service.NotificationOrchestrationService;
import sn.ondmoney.notificationservice.service.NotificationPreferenceService;
import sn.ondmoney.notificationservice.service.NotificationService;
import sn.ondmoney.notificationservice.service.dto.NotificationDTO;
import sn.ondmoney.notificationservice.service.dto.NotificationPreferenceDTO;
import sn.ondmoney.notificationservice.service.dto.NotificationRequestDTO;

/**
 * Endpoint SOAP pour le service de notifications
 *
 * Ce endpoint expose les opérations SOAP :
 * - SendNotification : Envoyer une notification
 * - GetNotificationStatus : Vérifier le statut d'une notification
 * - GetUserPreference : Récupérer les préférences utilisateur
 * - UpdateUserPreference : Mettre à jour les préférences
 *
 * URL du WSDL : http://localhost:8081/ws/notifications.wsdl
 * URL du endpoint : http://localhost:8081/ws
 */
@Endpoint
public class NotificationEndpoint {

    private static final String NAMESPACE_URI = "http://ondmoney.sn/notification/schemas";
    private final Logger log = LoggerFactory.getLogger(NotificationEndpoint.class);

    private final NotificationOrchestrationService orchestrationService;
    private final NotificationService notificationService;
    private final NotificationPreferenceService preferenceService;

    public NotificationEndpoint(
        NotificationOrchestrationService orchestrationService,
        NotificationService notificationService,
        NotificationPreferenceService preferenceService
    ) {
        this.orchestrationService = orchestrationService;
        this.notificationService = notificationService;
        this.preferenceService = preferenceService;
    }

    /**
     * Opération SOAP : Envoyer une notification
     *
     * Exemple de requête SOAP :
     * <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
     *                   xmlns:sch="http://ondmoney.sn/notification/schemas">
     *   <soapenv:Body>
     *     <sch:SendNotificationRequest>
     *       <sch:userId>USER123</sch:userId>
     *       <sch:type>TRANSACTION_SENT</sch:type>
     *       <sch:channels>SMS</sch:channels>
     *       <sch:title>Transaction effectuée</sch:title>
     *       <sch:message>Votre transaction a été effectuée avec succès</sch:message>
     *       <sch:priority>HIGH</sch:priority>
     *       <sch:immediate>true</sch:immediate>
     *     </sch:SendNotificationRequest>
     *   </soapenv:Body>
     * </soapenv:Envelope>
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SendNotificationRequest")
    @ResponsePayload
    public SendNotificationResponse sendNotification(@RequestPayload SendNotificationRequest request) {
        log.info("SOAP Request - Send Notification for user: {}", request.getUserId());

        SendNotificationResponse response = new SendNotificationResponse();

        try {
            // 1. Validation des paramètres
            validateSendNotificationRequest(request);

            // 2. Convertir la requête SOAP en DTO interne
            NotificationRequestDTO notificationRequest = convertToNotificationRequestDTO(request);

            // 3. Envoyer la notification via le service d'orchestration
            NotificationDTO result = orchestrationService.sendDirectNotification(notificationRequest);

            // 4. Construire la réponse SOAP
            response.setNotificationId(result.getId());
            response.setStatus(convertToSoapStatus(result.getStatus()));
            response.setMessage("Notification créée et envoyée avec succès");
            response.setTimestamp(convertToXMLGregorianCalendar(result.getCreatedAt()));

            log.info("SOAP Response - Notification sent successfully: id={}", result.getId());
        } catch (IllegalArgumentException e) {
            log.error("Validation error in send notification request", e);
            response.setNotificationId(-1L);
            response.setStatus(sn.ondmoney.notification.schemas.NotificationStatus.FAILED);
            response.setMessage("Erreur de validation: " + e.getMessage());
            response.setTimestamp(convertToXMLGregorianCalendar(Instant.now()));
        } catch (Exception e) {
            log.error("Error sending notification via SOAP", e);
            response.setNotificationId(-1L);
            response.setStatus(sn.ondmoney.notification.schemas.NotificationStatus.FAILED);
            response.setMessage("Erreur interne: " + e.getMessage());
            response.setTimestamp(convertToXMLGregorianCalendar(Instant.now()));
        }

        return response;
    }

    /**
     * Opération SOAP : Vérifier le statut d'une notification
     *
     * Exemple de requête :
     * <sch:GetNotificationStatusRequest>
     *   <sch:notificationId>123</sch:notificationId>
     * </sch:GetNotificationStatusRequest>
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetNotificationStatusRequest")
    @ResponsePayload
    public GetNotificationStatusResponse getNotificationStatus(@RequestPayload GetNotificationStatusRequest request) {
        log.info("SOAP Request - Get Status for notification: {}", request.getNotificationId());

        GetNotificationStatusResponse response = new GetNotificationStatusResponse();

        try {
            // Récupérer la notification depuis la base de données
            Optional<NotificationDTO> notificationOpt = notificationService.findOne(request.getNotificationId());

            if (notificationOpt.isEmpty()) {
                throw new RuntimeException("Notification non trouvée: " + request.getNotificationId());
            }

            NotificationDTO notification = notificationOpt.get();

            // Construire la réponse
            response.setNotificationId(notification.getId());
            response.setUserId(notification.getUserId());
            response.setType(convertToSoapType(notification.getType()));
            response.setStatus(convertToSoapStatus(notification.getStatus()));
            response.setRetryCount(notification.getRetryCount() != null ? notification.getRetryCount() : 0);

            if (notification.getSentAt() != null) {
                response.setSentAt(convertToXMLGregorianCalendar(notification.getSentAt()));
            }

            if (notification.getErrorMessage() != null) {
                response.setErrorMessage(notification.getErrorMessage());
            }

            log.info("SOAP Response - Notification status retrieved: id={}, status={}", notification.getId(), notification.getStatus());
        } catch (Exception e) {
            log.error("Error getting notification status", e);
            throw new RuntimeException("Erreur lors de la récupération du statut: " + e.getMessage());
        }

        return response;
    }

    /**
     * Opération SOAP : Récupérer les préférences utilisateur
     *
     * Exemple de requête :
     * <sch:GetUserPreferenceRequest>
     *   <sch:userId>USER123</sch:userId>
     * </sch:GetUserPreferenceRequest>
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetUserPreferenceRequest")
    @ResponsePayload
    public GetUserPreferenceResponse getUserPreference(@RequestPayload GetUserPreferenceRequest request) {
        log.info("SOAP Request - Get Preferences for user: {}", request.getUserId());

        GetUserPreferenceResponse response = new GetUserPreferenceResponse();

        try {
            // Récupérer les préférences
            Optional<NotificationPreferenceDTO> prefOpt = preferenceService.findByUserId(request.getUserId());

            if (prefOpt.isEmpty()) {
                // Créer des préférences par défaut si elles n'existent pas
                NotificationPreferenceDTO defaultPref = createDefaultPreferences(request.getUserId());
                prefOpt = Optional.of(preferenceService.save(defaultPref));
            }

            NotificationPreferenceDTO preference = prefOpt.get();

            // Construire la réponse
            response.setUserId(preference.getUserId());
            response.setSmsEnabled(preference.getSmsEnabled());
            response.setEmailEnabled(preference.getEmailEnabled());
            response.setPushEnabled(preference.getPushEnabled());
            response.setLanguage(preference.getLanguage());

            // Ajouter les types mutés
            if (preference.getMutedTypes() != null && !preference.getMutedTypes().isEmpty()) {
                List<sn.ondmoney.notification.schemas.NotificationType> mutedTypes = parseMutedTypes(preference.getMutedTypes());
                response.getMutedTypes().addAll(mutedTypes);
            }

            log.info("SOAP Response - Preferences retrieved for user: {}", request.getUserId());
        } catch (Exception e) {
            log.error("Error getting user preferences", e);
            throw new RuntimeException("Erreur lors de la récupération des préférences: " + e.getMessage());
        }

        return response;
    }

    /**
     * Opération SOAP : Mettre à jour les préférences utilisateur
     *
     * Exemple de requête :
     * <sch:UpdateUserPreferenceRequest>
     *   <sch:userId>USER123</sch:userId>
     *   <sch:smsEnabled>true</sch:smsEnabled>
     *   <sch:emailEnabled>true</sch:emailEnabled>
     *   <sch:pushEnabled>false</sch:pushEnabled>
     *   <sch:mutedTypes>LOW_BALANCE</sch:mutedTypes>
     * </sch:UpdateUserPreferenceRequest>
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "UpdateUserPreferenceRequest")
    @ResponsePayload
    public UpdateUserPreferenceResponse updateUserPreference(@RequestPayload UpdateUserPreferenceRequest request) {
        log.info("SOAP Request - Update Preferences for user: {}", request.getUserId());

        UpdateUserPreferenceResponse response = new UpdateUserPreferenceResponse();

        try {
            // Récupérer ou créer les préférences
            Optional<NotificationPreferenceDTO> prefOpt = preferenceService.findByUserId(request.getUserId());

            NotificationPreferenceDTO preference;
            if (prefOpt.isPresent()) {
                preference = prefOpt.get();
            } else {
                preference = new NotificationPreferenceDTO();
                preference.setUserId(request.getUserId());
                preference.setLanguage("fr"); // Langue par défaut
            }

            // Mettre à jour les préférences
            preference.setSmsEnabled(request.isSmsEnabled());
            preference.setEmailEnabled(request.isEmailEnabled());
            preference.setPushEnabled(request.isPushEnabled());

            // Mettre à jour les types mutés
            if (request.getMutedTypes() != null && !request.getMutedTypes().isEmpty()) {
                String mutedTypesJson = convertMutedTypesToJson(request.getMutedTypes());
                preference.setMutedTypes(mutedTypesJson);
            }

            preference.setUpdatedAt(Instant.now());

            // Sauvegarder
            preferenceService.save(preference);

            response.setSuccess(true);
            response.setMessage("Préférences mises à jour avec succès");

            log.info("SOAP Response - Preferences updated for user: {}", request.getUserId());
        } catch (Exception e) {
            log.error("Error updating user preferences", e);
            response.setSuccess(false);
            response.setMessage("Erreur: " + e.getMessage());
        }

        return response;
    }

    // ========================================================================
    // MÉTHODES UTILITAIRES
    // ========================================================================

    /**
     * Valide la requête d'envoi de notification
     */
    private void validateSendNotificationRequest(SendNotificationRequest request) {
        if (request.getUserId() == null || request.getUserId().isEmpty()) {
            throw new IllegalArgumentException("userId est obligatoire");
        }
        if (request.getType() == null) {
            throw new IllegalArgumentException("type est obligatoire");
        }
        if (request.getChannels() == null || request.getChannels().isEmpty()) {
            throw new IllegalArgumentException("Au moins un canal est obligatoire");
        }
        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            throw new IllegalArgumentException("title est obligatoire");
        }
        if (request.getMessage() == null || request.getMessage().isEmpty()) {
            throw new IllegalArgumentException("message est obligatoire");
        }
        if (request.getPriority() == null) {
            throw new IllegalArgumentException("priority est obligatoire");
        }
    }

    /**
     * Convertit une requête SOAP en DTO interne
     */
    private NotificationRequestDTO convertToNotificationRequestDTO(SendNotificationRequest request) {
        NotificationRequestDTO dto = new NotificationRequestDTO();
        dto.setUserId(request.getUserId());
        dto.setAccountNumber(request.getAccountNumber());

        // Convertir le type
        dto.setType(convertFromSoapType(request.getType()));

        // Convertir les canaux
        List<sn.ondmoney.notificationservice.domain.enumeration.NotificationChannel> channels = new ArrayList<>();
        for (sn.ondmoney.notification.schemas.NotificationChannel soapChannel : request.getChannels()) {
            channels.add(convertFromSoapChannel(soapChannel));
        }
        dto.setChannels(channels);

        dto.setTitle(request.getTitle());
        dto.setMessage(request.getMessage());
        dto.setPriority(convertFromSoapPriority(request.getPriority()));
        dto.setImmediate(request.isImmediate());
        dto.setMetadata(request.getMetadata());

        return dto;
    }

    /**
     * Convertit un type SOAP en type interne
     */
    private sn.ondmoney.notificationservice.domain.enumeration.NotificationType convertFromSoapType(
        sn.ondmoney.notification.schemas.NotificationType soapType
    ) {
        return sn.ondmoney.notificationservice.domain.enumeration.NotificationType.valueOf(soapType.name());
    }

    /**
     * Convertit un type interne en type SOAP
     */
    private sn.ondmoney.notification.schemas.NotificationType convertToSoapType(
        sn.ondmoney.notificationservice.domain.enumeration.NotificationType internalType
    ) {
        return sn.ondmoney.notification.schemas.NotificationType.valueOf(internalType.name());
    }

    /**
     * Convertit un canal SOAP en canal interne
     */
    private sn.ondmoney.notificationservice.domain.enumeration.NotificationChannel convertFromSoapChannel(
        sn.ondmoney.notification.schemas.NotificationChannel soapChannel
    ) {
        return sn.ondmoney.notificationservice.domain.enumeration.NotificationChannel.valueOf(soapChannel.name());
    }

    /**
     * Convertit un statut interne en statut SOAP
     */
    private sn.ondmoney.notification.schemas.NotificationStatus convertToSoapStatus(
        sn.ondmoney.notificationservice.domain.enumeration.NotificationStatus internalStatus
    ) {
        return sn.ondmoney.notification.schemas.NotificationStatus.valueOf(internalStatus.name());
    }

    /**
     * Convertit une priorité SOAP en priorité interne
     */
    private sn.ondmoney.notificationservice.domain.enumeration.Priority convertFromSoapPriority(
        sn.ondmoney.notification.schemas.Priority soapPriority
    ) {
        return sn.ondmoney.notificationservice.domain.enumeration.Priority.valueOf(soapPriority.name());
    }

    /**
     * Convertit un Instant en XMLGregorianCalendar pour SOAP
     */
    private XMLGregorianCalendar convertToXMLGregorianCalendar(Instant instant) {
        try {
            if (instant == null) {
                return null;
            }
            GregorianCalendar gcal = GregorianCalendar.from(instant.atZone(ZoneId.systemDefault()));
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        } catch (Exception e) {
            log.error("Error converting date", e);
            return null;
        }
    }

    /**
     * Crée des préférences par défaut pour un nouvel utilisateur
     */
    private NotificationPreferenceDTO createDefaultPreferences(String userId) {
        NotificationPreferenceDTO pref = new NotificationPreferenceDTO();
        pref.setUserId(userId);
        pref.setSmsEnabled(true);
        pref.setEmailEnabled(true);
        pref.setPushEnabled(true);
        pref.setLanguage("fr");
        pref.setMutedTypes("[]"); // Aucun type muté par défaut
        pref.setUpdatedAt(Instant.now());
        return pref;
    }

    /**
     * Parse les types mutés depuis JSON
     */
    private List<sn.ondmoney.notification.schemas.NotificationType> parseMutedTypes(String mutedTypesJson) {
        List<sn.ondmoney.notification.schemas.NotificationType> result = new ArrayList<>();

        try {
            // Nettoyer le JSON
            String cleaned = mutedTypesJson.replace("[", "").replace("]", "").replace("\"", "");
            if (!cleaned.isEmpty()) {
                String[] types = cleaned.split(",");
                for (String type : types) {
                    try {
                        result.add(sn.ondmoney.notification.schemas.NotificationType.valueOf(type.trim()));
                    } catch (IllegalArgumentException e) {
                        log.warn("Unknown notification type: {}", type);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error parsing muted types", e);
        }

        return result;
    }

    /**
     * Convertit les types mutés en JSON
     */
    private String convertMutedTypesToJson(List<sn.ondmoney.notification.schemas.NotificationType> mutedTypes) {
        if (mutedTypes == null || mutedTypes.isEmpty()) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < mutedTypes.size(); i++) {
            json.append("\"").append(mutedTypes.get(i).name()).append("\"");
            if (i < mutedTypes.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        return json.toString();
    }
}
