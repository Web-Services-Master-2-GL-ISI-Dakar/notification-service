package sn.ondmoney.notificationservice.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.ondmoney.notificationservice.service.dto.NotificationRequestDTO;

/**
 * Service Interface for managing {@link sn.ondmoney.notificationservice.domain.NotificationRequest}.
 */
public interface NotificationRequestService {
    /**
     * Save a notificationRequest.
     *
     * @param notificationRequestDTO the entity to save.
     * @return the persisted entity.
     */
    NotificationRequestDTO save(NotificationRequestDTO notificationRequestDTO);

    /**
     * Updates a notificationRequest.
     *
     * @param notificationRequestDTO the entity to update.
     * @return the persisted entity.
     */
    NotificationRequestDTO update(NotificationRequestDTO notificationRequestDTO);

    /**
     * Partially updates a notificationRequest.
     *
     * @param notificationRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<NotificationRequestDTO> partialUpdate(NotificationRequestDTO notificationRequestDTO);

    /**
     * Get all the notificationRequests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NotificationRequestDTO> findAll(Pageable pageable);

    /**
     * Get the "id" notificationRequest.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<NotificationRequestDTO> findOne(Long id);

    /**
     * Delete the "id" notificationRequest.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
