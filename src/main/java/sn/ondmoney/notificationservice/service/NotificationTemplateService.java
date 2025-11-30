package sn.ondmoney.notificationservice.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationType;
import sn.ondmoney.notificationservice.service.dto.NotificationTemplateDTO;

/**
 * Service Interface for managing {@link sn.ondmoney.notificationservice.domain.NotificationTemplate}.
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
    Optional<NotificationTemplateDTO> findOne(Long id);

    /**
     * Find template by type and language
     */
    Optional<NotificationTemplateDTO> findByTypeAndLanguage(NotificationType type, String language);

    /**
     * Delete the "id" notificationTemplate.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
