package sn.ondmoney.notification.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.ondmoney.notification.service.dto.NotificationLogDTO;

/**
 * Service Interface for managing {@link sn.ondmoney.notification.domain.NotificationLog}.
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
     * Get all the notificationLogs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NotificationLogDTO> findAll(Pageable pageable);

    /**
     * Get the "id" notificationLog.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<NotificationLogDTO> findOne(String id);

    /**
     * Delete the "id" notificationLog.
     *
     * @param id the id of the entity.
     */
    void delete(String id);

    /**
     * Search for the notificationLog corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NotificationLogDTO> search(String query, Pageable pageable);
}
