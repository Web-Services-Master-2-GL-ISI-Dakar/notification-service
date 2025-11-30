package sn.ondmoney.notificationservice.service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationChannel;
import sn.ondmoney.notificationservice.service.dto.NotificationPreferenceDTO;
import sn.ondmoney.notificationservice.service.dto.UserProfileDTO;

/**
 * Service de validation des canaux de notification
 *
 * Ce service vérifie :
 * - Si l'utilisateur a activé le canal dans ses préférences
 * - Si les informations nécessaires sont présentes (téléphone, email, token)
 * - Si les informations sont vérifiées
 */
@Service
public class ChannelValidatorService {

    private final Logger log = LoggerFactory.getLogger(ChannelValidatorService.class);

    /**
     * Valide et retourne les canaux disponibles pour un utilisateur
     *
     * @param profile Profil utilisateur avec ses coordonnées
     * @param preferences Préférences de notification de l'utilisateur
     * @return Liste des canaux disponibles
     */
    public List<NotificationChannel> validateAvailableChannels(UserProfileDTO profile, NotificationPreferenceDTO preferences) {
        log.debug("Validating notification channels for user: {}", profile.getUserId());

        List<NotificationChannel> availableChannels = new ArrayList<>();

        // Vérifier SMS
        if (isSmsAvailable(profile, preferences)) {
            availableChannels.add(NotificationChannel.SMS);
            log.debug("SMS channel available for user: {}", profile.getUserId());
        }

        // Vérifier Email
        if (isEmailAvailable(profile, preferences)) {
            availableChannels.add(NotificationChannel.EMAIL);
            log.debug("Email channel available for user: {}", profile.getUserId());
        }

        // Vérifier Push
        if (isPushAvailable(profile, preferences)) {
            availableChannels.add(NotificationChannel.PUSH);
            log.debug("Push channel available for user: {}", profile.getUserId());
        }

        log.debug("Available channels for user {}: {}", profile.getUserId(), availableChannels);
        return availableChannels;
    }

    /**
     * Vérifie si le SMS est disponible
     */
    private boolean isSmsAvailable(UserProfileDTO profile, NotificationPreferenceDTO preferences) {
        return (
            preferences.getSmsEnabled() != null &&
            preferences.getSmsEnabled() &&
            profile.getPhoneNumber() != null &&
            !profile.getPhoneNumber().isEmpty() &&
            profile.getPhoneVerified() != null &&
            profile.getPhoneVerified()
        );
    }

    /**
     * Vérifie si l'Email est disponible
     */
    private boolean isEmailAvailable(UserProfileDTO profile, NotificationPreferenceDTO preferences) {
        return (
            preferences.getEmailEnabled() != null &&
            preferences.getEmailEnabled() &&
            profile.getEmail() != null &&
            !profile.getEmail().isEmpty() &&
            profile.getEmailVerified() != null &&
            profile.getEmailVerified()
        );
    }

    /**
     * Vérifie si le Push est disponible
     */
    private boolean isPushAvailable(UserProfileDTO profile, NotificationPreferenceDTO preferences) {
        return (
            preferences.getPushEnabled() != null &&
            preferences.getPushEnabled() &&
            profile.getDeviceToken() != null &&
            !profile.getDeviceToken().isEmpty()
        );
    }

    /**
     * Valide un canal spécifique
     */
    public boolean isChannelValid(NotificationChannel channel, UserProfileDTO profile, NotificationPreferenceDTO preferences) {
        switch (channel) {
            case SMS:
                return isSmsAvailable(profile, preferences);
            case EMAIL:
                return isEmailAvailable(profile, preferences);
            case PUSH:
                return isPushAvailable(profile, preferences);
            default:
                return false;
        }
    }

    /**
     * Récupère le destinataire selon le canal
     */
    public String getRecipient(NotificationChannel channel, UserProfileDTO profile) {
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
}
