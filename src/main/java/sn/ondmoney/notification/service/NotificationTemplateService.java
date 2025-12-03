package sn.ondmoney.notification.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.ondmoney.notification.domain.enumeration.NotificationChannel;
import sn.ondmoney.notification.domain.enumeration.NotificationLanguage;
import sn.ondmoney.notification.domain.enumeration.NotificationType;
import sn.ondmoney.notification.service.dto.NotificationTemplateDTO;

/**
 * Service Interface for managing {@link sn.ondmoney.notification.domain.NotificationTemplate}.
 */
public interface NotificationTemplateService {
    /**
     * Save a notificationTemplate.
     *
     * @param notificationTemplateDTO the entity to save.
     * @return the persisted entity.
     */
    NotificationTemplateDTO save(NotificationTemplateDTO notificationTemplateDTO);

    /**
     * Updates a notificationTemplate.
     *
     * @param notificationTemplateDTO the entity to update.
     * @return the persisted entity.
     */
    NotificationTemplateDTO update(NotificationTemplateDTO notificationTemplateDTO);

    /**
     * Partially updates a notificationTemplate.
     *
     * @param notificationTemplateDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<NotificationTemplateDTO> partialUpdate(NotificationTemplateDTO notificationTemplateDTO);

    /**
     * Get all the notificationTemplates.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NotificationTemplateDTO> findAll(Pageable pageable);

    /**
     * Get the "id" notificationTemplate.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<NotificationTemplateDTO> findOne(String id);

    /**
     * Delete the "id" notificationTemplate.
     *
     * @param id the id of the entity.
     */
    void delete(String id);

    /**
     * Search for the notificationTemplate corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NotificationTemplateDTO> search(String query, Pageable pageable);

    Optional<NotificationTemplateDTO> findActiveTemplateByCompositeKey(
        NotificationType eventType,
        NotificationChannel channel,
        NotificationLanguage lang,
        int version
    );
}
