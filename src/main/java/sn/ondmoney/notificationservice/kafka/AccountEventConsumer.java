package sn.ondmoney.notificationservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import sn.ondmoney.notificationservice.service.NotificationOrchestrationService;
import sn.ondmoney.notificationservice.service.dto.AccountEventDTO;

/**
 * Consumer Kafka pour les événements de compte
 */
@Service
public class AccountEventConsumer {

    private final Logger log = LoggerFactory.getLogger(AccountEventConsumer.class);
    private final NotificationOrchestrationService orchestrationService;

    public AccountEventConsumer(NotificationOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @KafkaListener(
        topics = "${application.kafka.topics.accounts}",
        groupId = "notification-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeAccountEvent(AccountEventDTO event, Acknowledgment acknowledgment) {
        log.debug("Received account event from Kafka: {}", event);

        try {
            orchestrationService.processAccountEvent(event);
            acknowledgment.acknowledge();
            log.info("Successfully processed account event: {}", event.getAccountId());
        } catch (Exception e) {
            log.error("Error processing account event: {}", event.getAccountId(), e);
        }
    }
}
