package sn.ondmoney.notificationservice.service;

import java.util.Optional;
import sn.ondmoney.notificationservice.service.dto.NotificationLogDTO;

/**
 * Service Interface for managing {@link sn.ondmoney.notificationservice.domain.NotificationLog}.
 */
public interface NotificationLogService {
    /**
     * Save a notificationLog.
     *
     * @param notificationLogDTO the entity to save.
     * @return the persisted entity.
     */
    NotificationLogDTO save(NotificationLogDTO notificationLogDTO);

    /**
     * Updates a notificationLog.
     *
     * @param notificationLogDTO the entity to update.
     * @return the persisted entity.
     */
    NotificationLogDTO update(NotificationLogDTO notificationLogDTO);

    /**
     * Partially updates a notificationLog.
     *
     * @param notificationLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<NotificationLogDTO> partialUpdate(NotificationLogDTO notificationLogDTO);

    /**
     * Get the "id" notificationLog.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<NotificationLogDTO> findOne(Long id);

    /**
     * Delete the "id" notificationLog.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
