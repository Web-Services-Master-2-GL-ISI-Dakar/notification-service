package sn.ondmoney.notificationservice.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationType;
import sn.ondmoney.notificationservice.service.dto.NotificationPreferenceDTO;

/**
 * Service Interface for managing {@link sn.ondmoney.notificationservice.domain.NotificationPreference}.
 */
public interface NotificationPreferenceService {
    /**
     * Save a notificationPreference.
     *
     * @param notificationPreferenceDTO the entity to save.
     * @return the persisted entity.
     */
    NotificationPreferenceDTO save(NotificationPreferenceDTO notificationPreferenceDTO);

    /**
     * Updates a notificationPreference.
     *
     * @param notificationPreferenceDTO the entity to update.
     * @return the persisted entity.
     */
    NotificationPreferenceDTO update(NotificationPreferenceDTO notificationPreferenceDTO);

    /**
     * Partially updates a notificationPreference.
     *
     * @param notificationPreferenceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<NotificationPreferenceDTO> partialUpdate(NotificationPreferenceDTO notificationPreferenceDTO);

    /**
     * Get all the notificationPreferences.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NotificationPreferenceDTO> findAll(Pageable pageable);

    /**
     * Get the "id" notificationPreference.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<NotificationPreferenceDTO> findOne(Long id);

    /**
     * Get notificationPreference by userId.
     *
     * @param userId the userId.
     * @return the entity.
     */
    Optional<NotificationPreferenceDTO> findByUserId(String userId);

    /**
     * Check if notificationPreference exists for userId.
     *
     * @param userId the userId.
     * @return true if exists.
     */
    boolean existsByUserId(String userId);

    /**
     * Create default preferences for a new user.
     *
     * @param userId the userId.
     * @return the created entity.
     */
    NotificationPreferenceDTO createDefaultPreferences(String userId);

    /**
     * Set SMS enabled/disabled.
     *
     * @param userId the userId.
     * @param enabled true to enable, false to disable.
     */
    void setSmsEnabled(String userId, boolean enabled);

    /**
     * Set Email enabled/disabled.
     *
     * @param userId the userId.
     * @param enabled true to enable, false to disable.
     */
    void setEmailEnabled(String userId, boolean enabled);

    /**
     * Set Push enabled/disabled.
     *
     * @param userId the userId.
     * @param enabled true to enable, false to disable.
     */
    void setPushEnabled(String userId, boolean enabled);

    /**
     * Mute a notification type.
     *
     * @param userId the userId.
     * @param type the notification type to mute.
     */
    void muteNotificationType(String userId, NotificationType type);

    /**
     * Unmute a notification type.
     *
     * @param userId the userId.
     * @param type the notification type to unmute.
     */
    void unmuteNotificationType(String userId, NotificationType type);

    /**
     * Check if a notification type is muted.
     *
     * @param userId the userId.
     * @param type the notification type.
     * @return true if muted.
     */
    boolean isTypeMuted(String userId, NotificationType type);

    /**
     * Set user language.
     *
     * @param userId the userId.
     * @param language the language code.
     */
    void setLanguage(String userId, String language);

    /**
     * Delete the "id" notificationPreference.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Delete by userId.
     *
     * @param userId the userId.
     */
    void deleteByUserId(String userId);
}
