package sn.ondmoney.notification.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sn.ondmoney.notification.domain.NotificationLog;
import sn.ondmoney.notification.repository.NotificationLogRepository;
import sn.ondmoney.notification.repository.search.NotificationLogSearchRepository;
import sn.ondmoney.notification.service.dto.NotificationLogDTO;

@Component("notificationEventConsumer")
public class NotificationEventConsumer implements Consumer<String> {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final NotificationLogRepository repository;
    private final NotificationEventProducer notificationEventProducer;
    private final ObjectMapper objectMapper;
    
    // Elasticsearch est optionnel en dev
    @Autowired(required = false)
    private NotificationLogSearchRepository notificationLogSearchRepository;

    public NotificationEventConsumer(
        NotificationLogRepository repository,
        NotificationEventProducer notificationEventProducer,
        ObjectMapper objectMapper
    ) {
        this.repository = repository;
        this.notificationEventProducer = notificationEventProducer;
        this.objectMapper = objectMapper;
    }

    @Override
    public void accept(String payload) {
        LOG.info("<<< Consumed notification event: {}", payload);
        NotificationLog savedNtf = null;
        NotificationLog tx = null;
        try {
            tx = objectMapper.readValue(payload, NotificationLog.class);
        } catch (JsonProcessingException e) {
            LOG.error("Failed to deserialize notification event: {}", payload, e);
            throw new RuntimeException(e);
        }
        try {
            // --- Idempotency check ---
            if (repository.existsByEventRef(tx.getEventRef())) {
                LOG.warn("Duplicate notification ignored: {}", tx.getEventRef());
                return;
            }

            // --- Persist Notification in MongoDB ---
            tx.setCreatedAt(Instant.now());
            tx.setSentAt(Instant.now());
            savedNtf = repository.save(tx);
            LOG.info("Notification saved successfully in MongoDB: {}", savedNtf.getEventRef());

            // --- Index Notification in Elasticsearch (optionnel) ---
            if (notificationLogSearchRepository != null) {
                notificationLogSearchRepository.index(savedNtf);
                LOG.info("Notification indexed successfully in Elasticsearch: {}", savedNtf.getEventRef());
            } else {
                LOG.debug("Elasticsearch not available, skipping indexing");
            }

            // --- Publish success event ---
            NotificationLogDTO successEvent = new NotificationLogDTO();
            successEvent.setNotificationType(savedNtf.getNotificationType());
            successEvent.setEventRef(savedNtf.getEventRef());
            successEvent.setSentAt(Instant.now());
            successEvent.setCreatedAt(Instant.now());
            successEvent.setUserId(savedNtf.getUserId());
            successEvent.setRecipient(savedNtf.getRecipient());
            successEvent.setNotificationStatus(savedNtf.getNotificationStatus());
            successEvent.setRetryCount(savedNtf.getRetryCount());
            successEvent.setErrorMessage(savedNtf.getErrorMessage());
            successEvent.setExternalEventRef(savedNtf.getExternalEventRef());
            notificationEventProducer.publish(successEvent);
        } catch (Exception e) {
            LOG.error("Error processing notification event: {}", payload, e);

            try {
                NotificationLogDTO failedEvent = new NotificationLogDTO();
                assert savedNtf != null;
                failedEvent.setEventRef(savedNtf.getEventRef());
                failedEvent.setNotificationType(savedNtf.getNotificationType());
                failedEvent.setCreatedAt(Instant.now());
                failedEvent.setSentAt(Instant.now());
                failedEvent.setErrorMessage(savedNtf.getErrorMessage());
                failedEvent.setErrorMessage("Failed to save or index notification: " + e.getMessage());
                notificationEventProducer.publish(failedEvent);
            } catch (Exception ex) {
                LOG.error("Failed to send failure event", ex);
            }
        }
    }
}
