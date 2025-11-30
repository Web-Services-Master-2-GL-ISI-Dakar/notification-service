package sn.ondmoney.notificationservice.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.ondmoney.notificationservice.service.dto.TransactionEventDTO;

/**
 * Service Interface for managing {@link sn.ondmoney.notificationservice.domain.TransactionEvent}.
 */
public interface TransactionEventService {
    /**
     * Save a transactionEvent.
     *
     * @param transactionEventDTO the entity to save.
     * @return the persisted entity.
     */
    TransactionEventDTO save(TransactionEventDTO transactionEventDTO);

    /**
     * Updates a transactionEvent.
     *
     * @param transactionEventDTO the entity to update.
     * @return the persisted entity.
     */
    TransactionEventDTO update(TransactionEventDTO transactionEventDTO);

    /**
     * Partially updates a transactionEvent.
     *
     * @param transactionEventDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TransactionEventDTO> partialUpdate(TransactionEventDTO transactionEventDTO);

    /**
     * Get all the transactionEvents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionEventDTO> findAll(Pageable pageable);

    /**
     * Get the "id" transactionEvent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TransactionEventDTO> findOne(Long id);

    /**
     * Delete the "id" transactionEvent.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
