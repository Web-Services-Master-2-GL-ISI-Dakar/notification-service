package sn.ondmoney.notificationservice.service;

import java.time.Instant;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.notificationservice.domain.enumeration.*;
import sn.ondmoney.notificationservice.service.dto.*;

/**
 * Service d'orchestration des notifications
 *
 * Ce service est le cerveau du microservice :
 * - Il reçoit les événements (Kafka ou SOAP)
 * - Il détermine qui notifier et comment
 * - Il crée et envoie les notifications
 */
@Service
@Transactional
public class NotificationOrchestrationService {

    private final Logger log = LoggerFactory.getLogger(NotificationOrchestrationService.class);

    private final NotificationService notificationService;
    private final UserProfileService userProfileService;
    private final NotificationPreferenceService preferenceService;
    private final TemplateManagerService templateManager;
    private final ChannelValidatorService channelValidator;
    private final NotificationSenderService senderService;

    public NotificationOrchestrationService(
        NotificationService notificationService,
        UserProfileService userProfileService,
        NotificationPreferenceService preferenceService,
        TemplateManagerService templateManager,
        ChannelValidatorService channelValidator,
        NotificationSenderService senderService
    ) {
        this.notificationService = notificationService;
        this.userProfileService = userProfileService;
        this.preferenceService = preferenceService;
        this.templateManager = templateManager;
        this.channelValidator = channelValidator;
        this.senderService = senderService;
    }

    /**
     * Traite un événement de transaction depuis Kafka
     *
     * Exemple : Quand une transaction est effectuée, on notifie :
     * - L'expéditeur : "Vous avez envoyé X FCFA"
     * - Le destinataire : "Vous avez reçu X FCFA"
     */
    public void processTransactionEvent(TransactionEventDTO event) {
        log.debug("Processing transaction event: {}", event.getTransactionId());

        try {
            // 1. Identifier les utilisateurs à notifier
            List<String> usersToNotify = new ArrayList<>();

            // Notifier l'expéditeur
            if (event.getSenderUserId() != null) {
                usersToNotify.add(event.getSenderUserId());
            }

            // Notifier le destinataire
            if (event.getReceiverUserId() != null) {
                usersToNotify.add(event.getReceiverUserId());
            }

            // 2. Pour chaque utilisateur, créer une notification
            for (String userId : usersToNotify) {
                processTransactionNotificationForUser(userId, event);
            }
        } catch (Exception e) {
            log.error("Error processing transaction event", e);
            throw new RuntimeException("Failed to process transaction event", e);
        }
    }

    /**
     * Crée une notification de transaction pour un utilisateur spécifique
     */
    private void processTransactionNotificationForUser(String userId, TransactionEventDTO event) {
        log.debug("Creating transaction notification for user: {}", userId);

        // 1. Récupérer le profil utilisateur
        Optional<UserProfileDTO> profileOpt = userProfileService.findByUserId(userId);
        if (profileOpt.isEmpty()) {
            log.warn("User profile not found for userId: {}", userId);
            return;
        }
        UserProfileDTO profile = profileOpt.get();

        // 2. Récupérer les préférences
        Optional<NotificationPreferenceDTO> prefOpt = preferenceService.findByUserId(userId);
        if (prefOpt.isEmpty()) {
            log.warn("Notification preferences not found for userId: {}", userId);
            return;
        }
        NotificationPreferenceDTO preferences = prefOpt.get();

        // 3. Déterminer le type de notification
        NotificationType type = determineTransactionNotificationType(event, userId);

        // 4. Vérifier si l'utilisateur accepte ce type de notification
        if (preferences.getMutedTypes() != null && preferences.getMutedTypes().contains(type.toString())) {
            log.debug("User {} has muted notification type {}", userId, type);
            return;
        }

        // 5. Valider les canaux disponibles
        List<NotificationChannel> availableChannels = channelValidator.validateAvailableChannels(profile, preferences);

        if (availableChannels.isEmpty()) {
            log.warn("No available notification channels for user: {}", userId);
            return;
        }

        // 6. Préparer les données pour le template
        Map<String, Object> templateData = buildTransactionTemplateData(event, userId);

        // 7. Pour chaque canal disponible, créer et envoyer une notification
        for (NotificationChannel channel : availableChannels) {
            try {
                createAndSendNotification(userId, type, channel, templateData, event.getTransactionId());
            } catch (Exception e) {
                log.error("Failed to send notification via {}", channel, e);
            }
        }
    }

    /**
     * Détermine le type de notification selon le rôle de l'utilisateur
     */
    private NotificationType determineTransactionNotificationType(TransactionEventDTO event, String userId) {
        if (userId.equals(event.getSenderUserId())) {
            return NotificationType.TRANSACTION_SENT;
        } else if (userId.equals(event.getReceiverUserId())) {
            return NotificationType.TRANSACTION_RECEIVED;
        }
        return NotificationType.TRANSACTION_SENT; // Par défaut
    }

    /**
     * Construit les données pour remplir le template de notification
     */
    private Map<String, Object> buildTransactionTemplateData(TransactionEventDTO event, String userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("transactionId", event.getTransactionId());
        data.put("amount", event.getAmount());
        data.put("currency", event.getCurrency() != null ? event.getCurrency() : "FCFA");
        data.put("timestamp", event.getTimestamp());

        if (userId.equals(event.getSenderUserId())) {
            data.put("recipient", event.getReceiverAccount());
            data.put("action", "envoyé");
        } else {
            data.put("sender", event.getSenderAccount());
            data.put("action", "reçu");
        }

        return data;
    }

    /**
     * Crée et envoie une notification
     */
    private void createAndSendNotification(
        String userId,
        NotificationType type,
        NotificationChannel channel,
        Map<String, Object> templateData,
        String referenceId
    ) {
        // 1. Récupérer et rendre le template
        String message = templateManager.renderTemplate(type, channel, templateData);
        String title = templateManager.getTitle(type, templateData);

        // 2. Créer l'entité Notification
        NotificationDTO notification = new NotificationDTO();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setChannel(channel);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setRetryCount(0);
        notification.setCreatedAt(Instant.now());

        // Stocker le referenceId dans les metadata
        notification.setMetadata("{\"referenceId\":\"" + referenceId + "\"}");

        // 3. Sauvegarder en base de données
        NotificationDTO savedNotification = notificationService.save(notification);

        // 4. Envoyer la notification de manière asynchrone
        senderService.sendAsync(savedNotification);

        log.info("Notification created and queued: id={}, type={}, channel={}", savedNotification.getId(), type, channel);
    }

    /**
     * Traite un événement de compte
     */
    public void processAccountEvent(AccountEventDTO event) {
        log.debug("Processing account event: {} for user: {}", event.getEventType(), event.getUserId());

        NotificationType type;

        switch (event.getEventType()) {
            case "CREATED":
                type = NotificationType.ACCOUNT_CREATED;
                break;
            default:
                log.debug("Unhandled account event type: {}", event.getEventType());
                return;
        }

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("accountNumber", event.getAccountNumber());
        templateData.put("timestamp", event.getTimestamp());

        // Créer les notifications pour cet événement
        processSimpleNotification(event.getUserId(), type, templateData);
    }

    /**
     * Traite un événement de sécurité
     */
    public void processSecurityEvent(SecurityEventDTO event) {
        log.debug("Processing security event: {} for user: {}", event.getEventType(), event.getUserId());

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("eventType", event.getEventType());
        templateData.put("description", event.getDescription());
        templateData.put("ipAddress", event.getIpAddress());
        templateData.put("timestamp", event.getTimestamp());

        // Les alertes de sécurité sont toujours envoyées (pas de filtre de préférences)
        processUrgentNotification(event.getUserId(), NotificationType.SECURITY_ALERT, templateData);
    }

    /**
     * Traite une notification simple (non urgente)
     */
    private void processSimpleNotification(String userId, NotificationType type, Map<String, Object> templateData) {
        Optional<UserProfileDTO> profileOpt = userProfileService.findByUserId(userId);
        Optional<NotificationPreferenceDTO> prefOpt = preferenceService.findByUserId(userId);

        if (profileOpt.isEmpty() || prefOpt.isEmpty()) {
            log.warn("Cannot send notification - missing profile or preferences for user: {}", userId);
            return;
        }

        UserProfileDTO profile = profileOpt.get();
        NotificationPreferenceDTO preferences = prefOpt.get();

        List<NotificationChannel> channels = channelValidator.validateAvailableChannels(profile, preferences);

        for (NotificationChannel channel : channels) {
            createAndSendNotification(userId, type, channel, templateData, null);
        }
    }

    /**
     * Traite une notification urgente (envoyée à tous les canaux)
     */
    private void processUrgentNotification(String userId, NotificationType type, Map<String, Object> templateData) {
        Optional<UserProfileDTO> profileOpt = userProfileService.findByUserId(userId);

        if (profileOpt.isEmpty()) {
            log.warn("Cannot send urgent notification - missing profile for user: {}", userId);
            return;
        }

        UserProfileDTO profile = profileOpt.get();

        // Pour les notifications urgentes, on essaie tous les canaux possibles
        List<NotificationChannel> allChannels = Arrays.asList(NotificationChannel.SMS, NotificationChannel.EMAIL, NotificationChannel.PUSH);

        for (NotificationChannel channel : allChannels) {
            if (isChannelAvailable(profile, channel)) {
                createAndSendNotification(userId, type, channel, templateData, null);
            }
        }
    }

    /**
     * Vérifie si un canal est disponible pour un profil
     */
    private boolean isChannelAvailable(UserProfileDTO profile, NotificationChannel channel) {
        switch (channel) {
            case SMS:
                return profile.getPhoneVerified() && profile.getPhoneNumber() != null;
            case EMAIL:
                return profile.getEmailVerified() && profile.getEmail() != null;
            case PUSH:
                return profile.getDeviceToken() != null;
            default:
                return false;
        }
    }

    /**
     * Envoie une notification directe (appelée depuis SOAP)
     *
     * @param request La requête de notification
     * @return La notification créée
     */
    public NotificationDTO sendDirectNotification(NotificationRequestDTO request) {
        log.debug("Processing direct notification request for user: {}", request.getUserId());

        try {
            // 1. Récupérer le profil utilisateur
            Optional<UserProfileDTO> profileOpt = userProfileService.findByUserId(request.getUserId());
            if (profileOpt.isEmpty()) {
                throw new RuntimeException("Profil utilisateur non trouvé: " + request.getUserId());
            }
            UserProfileDTO profile = profileOpt.get();

            // 2. Récupérer les préférences
            Optional<NotificationPreferenceDTO> prefOpt = preferenceService.findByUserId(request.getUserId());
            if (prefOpt.isEmpty()) {
                // Créer des préférences par défaut
                prefOpt = Optional.of(preferenceService.createDefaultPreferences(request.getUserId()));
            }
            NotificationPreferenceDTO preferences = prefOpt.get();

            // 3. Valider les canaux demandés
            List<NotificationChannel> validChannels = new ArrayList<>();
            for (NotificationChannel channel : request.getChannels()) {
                if (channelValidator.isChannelValid(channel, profile, preferences)) {
                    validChannels.add(channel);
                } else {
                    log.warn("Channel {} not available for user {}", channel, request.getUserId());
                }
            }

            if (validChannels.isEmpty()) {
                throw new RuntimeException("Aucun canal de notification disponible pour l'utilisateur");
            }

            // 4. Créer et envoyer une notification pour chaque canal
            List<NotificationDTO> createdNotifications = new ArrayList<>();

            for (NotificationChannel channel : validChannels) {
                // Créer la notification
                NotificationDTO notification = new NotificationDTO();
                notification.setUserId(request.getUserId());
                notification.setAccountNumber(request.getAccountNumber());
                notification.setType(request.getType());
                notification.setChannel(channel);
                notification.setTitle(request.getTitle());
                notification.setMessage(request.getMessage());
                notification.setStatus(NotificationStatus.PENDING);
                notification.setRetryCount(0);
                notification.setCreatedAt(Instant.now());
                notification.setMetadata(request.getMetadata());

                // Déterminer le destinataire selon le canal
                String recipient = channelValidator.getRecipient(channel, profile);
                notification.setRecipient(recipient);

                // Sauvegarder
                NotificationDTO savedNotification = notificationService.save(notification);
                createdNotifications.add(savedNotification);

                // Envoyer selon la priorité
                if (request.getImmediate() != null && request.getImmediate()) {
                    senderService.sendSync(savedNotification);
                } else {
                    senderService.sendAsync(savedNotification);
                }
            }

            // Retourner la première notification créée (pour la réponse SOAP)
            return createdNotifications.isEmpty() ? null : createdNotifications.get(0);
        } catch (Exception e) {
            log.error("Error processing direct notification", e);
            throw new RuntimeException("Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
    }
}
