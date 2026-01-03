package sn.ondmoney.notification.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sn.ondmoney.notification.domain.ProcessedEvent;

import java.time.Instant;
import java.util.List;

/**
 * Repository for ProcessedEvent - tracks processed Kafka events for idempotency.
 */
@Repository
public interface ProcessedEventRepository extends MongoRepository<ProcessedEvent, String> {

    /**
     * Check if an event has already been processed.
     */
    boolean existsByEventId(String eventId);

    /**
     * Find events processed before a given time (for cleanup).
     */
    List<ProcessedEvent> findByProcessedAtBefore(Instant before);

    /**
     * Delete events processed before a given time.
     */
    void deleteByProcessedAtBefore(Instant before);
}
