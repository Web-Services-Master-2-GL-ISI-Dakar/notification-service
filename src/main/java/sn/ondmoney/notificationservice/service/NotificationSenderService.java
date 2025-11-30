package sn.ondmoney.notificationservice.service;

import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationChannel;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationStatus;
import sn.ondmoney.notificationservice.service.dto.NotificationDTO;
import sn.ondmoney.notificationservice.service.dto.UserProfileDTO;
import sn.ondmoney.notificationservice.service.handler.EmailHandler;
import sn.ondmoney.notificationservice.service.handler.PushHandler;
import sn.ondmoney.notificationservice.service.handler.SmsHandler;

/**
 * Service d'envoi de notifications
 *
 * Ce service :
 * - Dispatche les notifications vers le bon handler (SMS/Email/Push)
 * - Gère les retries en cas d'échec
 * - Met à jour le statut des notifications
 * - Crée les logs
 */
@Service
@Transactional
public class NotificationSenderService {

    private final Logger log = LoggerFactory.getLogger(NotificationSenderService.class);

    private final NotificationService notificationService;
    private final UserProfileService userProfileService;
    private final NotificationLogService logService;
    private final SmsHandler smsHandler;
    private final EmailHandler emailHandler;
    private final PushHandler pushHandler;

    public NotificationSenderService(
        NotificationService notificationService,
        UserProfileService userProfileService,
        NotificationLogService logService,
        SmsHandler smsHandler,
        EmailHandler emailHandler,
        PushHandler pushHandler
    ) {
        this.notificationService = notificationService;
        this.userProfileService = userProfileService;
        this.logService = logService;
        this.smsHandler = smsHandler;
        this.emailHandler = emailHandler;
        this.pushHandler = pushHandler;
    }

    /**
     * Envoie une notification de manière asynchrone
     *
     * @param notification La notification à envoyer
     */
    @Async
    public void sendAsync(NotificationDTO notification) {
        log.debug("Sending notification asynchronously: {}", notification.getId());
        send(notification);
    }

    /**
     * Envoie une notification de manière synchrone
     *
     * @param notification La notification à envoyer
     */
    public void sendSync(NotificationDTO notification) {
        log.debug("Sending notification synchronously: {}", notification.getId());
        send(notification);
    }

    /**
     * Logique principale d'envoi
     */
    private void send(NotificationDTO notification) {
        try {
            // 1. Récupérer le profil utilisateur pour avoir les coordonnées
            Optional<UserProfileDTO> profileOpt = userProfileService.findByUserId(notification.getUserId());

            if (profileOpt.isEmpty()) {
                log.error("Cannot send notification - user profile not found: {}", notification.getUserId());
                updateNotificationStatus(notification, NotificationStatus.FAILED, "Profil utilisateur introuvable");
                return;
            }

            UserProfileDTO profile = profileOpt.get();

            // 2. Déterminer le destinataire selon le canal
            String recipient = getRecipient(notification.getChannel(), profile);
            if (recipient == null) {
                log.error("Cannot send notification - recipient not found for channel: {}", notification.getChannel());
                updateNotificationStatus(notification, NotificationStatus.FAILED, "Destinataire non trouvé");
                return;
            }

            notification.setRecipient(recipient);

            // 3. Dispatcher vers le bon handler
            boolean success = dispatchToHandler(notification);

            // 4. Mettre à jour le statut
            if (success) {
                updateNotificationStatus(notification, NotificationStatus.SENT, null);
                log.info("Notification sent successfully: id={}, channel={}", notification.getId(), notification.getChannel());
            } else {
                handleSendFailure(notification);
            }
        } catch (Exception e) {
            log.error("Error sending notification: {}", notification.getId(), e);
            handleSendFailure(notification);
        }
    }

    /**
     * Dispatche la notification vers le handler approprié
     */
    private boolean dispatchToHandler(NotificationDTO notification) {
        try {
            switch (notification.getChannel()) {
                case SMS:
                    return smsHandler.send(notification);
                case EMAIL:
                    return emailHandler.send(notification);
                case PUSH:
                    return pushHandler.send(notification);
                default:
                    log.error("Unknown notification channel: {}", notification.getChannel());
                    return false;
            }
        } catch (Exception e) {
            log.error("Handler error for channel {}", notification.getChannel(), e);
            return false;
        }
    }

    /**
     * Récupère le destinataire selon le canal
     */
    private String getRecipient(NotificationChannel channel, UserProfileDTO profile) {
        switch (channel) {
            case SMS:
                return profile.getPhoneNumber();
            case EMAIL:
                return profile.getEmail();
            case PUSH:
                return profile.getDeviceToken();
            default:
                return null;
        }
    }

    /**
     * Gère l'échec d'envoi (avec retry)
     */
    private void handleSendFailure(NotificationDTO notification) {
        int retryCount = notification.getRetryCount() != null ? notification.getRetryCount() : 0;
        int maxRetries = 3; // TODO: Configurable

        if (retryCount < maxRetries) {
            // Incrémenter le compteur de retry
            notification.setRetryCount(retryCount + 1);
            notification.setStatus(NotificationStatus.RETRY);
            notificationService.save(notification);

            log.warn("Notification failed, will retry: id={}, attempt={}/{}", notification.getId(), retryCount + 1, maxRetries);
            // TODO: Implémenter une queue de retry avec délai exponentiel

        } else {
            // Max retries atteint, marquer comme échoué
            updateNotificationStatus(notification, NotificationStatus.FAILED, "Nombre maximum de tentatives atteint");

            log.error("Notification failed after {} retries: id={}", maxRetries, notification.getId());
        }
    }

    /**
     * Met à jour le statut de la notification
     */
    private void updateNotificationStatus(NotificationDTO notification, NotificationStatus status, String errorMessage) {
        notification.setStatus(status);
        notification.setErrorMessage(errorMessage);

        if (status == NotificationStatus.SENT) {
            notification.setSentAt(Instant.now());
        }

        notificationService.save(notification);

        // Créer un log
        createNotificationLog(notification, status, errorMessage);
    }

    /**
     * Crée un log de notification
     */
    private void createNotificationLog(NotificationDTO notification, NotificationStatus status, String errorMessage) {
        // TODO: Implémenter la création du log
        log.debug("Creating notification log: notificationId={}, status={}", notification.getId(), status);
    }
}
