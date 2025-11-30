package sn.ondmoney.notificationservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import sn.ondmoney.notificationservice.service.NotificationOrchestrationService;
import sn.ondmoney.notificationservice.service.dto.TransactionEventDTO;

/**
 * Consumer Kafka pour les événements de transaction
 *
 * Ce service écoute le topic "transactions" et traite chaque événement
 * en créant les notifications appropriées
 */
@Service
public class TransactionEventConsumer {

    private final Logger log = LoggerFactory.getLogger(TransactionEventConsumer.class);
    private final NotificationOrchestrationService orchestrationService;

    public TransactionEventConsumer(NotificationOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    /**
     * Écoute les événements de transaction depuis Kafka
     *
     * @param event L'événement de transaction reçu
     * @param acknowledgment Pour confirmer le traitement du message
     */
    @KafkaListener(
        topics = "${application.kafka.topics.transactions}",
        groupId = "notification-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTransactionEvent(TransactionEventDTO event, Acknowledgment acknowledgment) {
        log.debug("Received transaction event from Kafka: {}", event);

        try {
            // Traiter l'événement et créer les notifications
            orchestrationService.processTransactionEvent(event);

            // Confirmer que le message a été traité avec succès
            acknowledgment.acknowledge();

            log.info("Successfully processed transaction event: {}", event.getTransactionId());
        } catch (Exception e) {
            log.error("Error processing transaction event: {}", event.getTransactionId(), e);
            // En cas d'erreur, le message sera retraité ou envoyé au DLQ
            // selon la configuration du retry policy
        }
    }
}
