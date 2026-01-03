package sn.ondmoney.notification.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import sn.ondmoney.notification.domain.ProcessedEvent;
import sn.ondmoney.notification.repository.ProcessedEventRepository;
import sn.ondmoney.notification.service.SmsService;

/**
 * Kafka consumer for OTP requested events.
 * Sends SMS with OTP code when receiving events from AuthMS.
 * Implements idempotency by tracking processed events.
 */
@Component
public class OtpRequestedConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(OtpRequestedConsumer.class);
    private static final String TOPIC = "otp.requested";

    private final SmsService smsService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public OtpRequestedConsumer(
            SmsService smsService,
            ProcessedEventRepository processedEventRepository,
            ObjectMapper objectMapper) {
        this.smsService = smsService;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
        topics = TOPIC,
        groupId = "notification-consumers",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOtpRequested(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(value = "ce_id", required = false) String eventId) {
        
        LOG.info("Received otp.requested event for key: {}", maskPhone(key));

        try {
            // Parse CloudEvents envelope
            JsonNode cloudEvent = objectMapper.readTree(payload);
            
            // Extract event ID from CloudEvents envelope if not in header
            if (eventId == null || eventId.isBlank()) {
                eventId = cloudEvent.has("id") ? cloudEvent.get("id").asText() : key + "-" + System.currentTimeMillis();
            }

            // Idempotency check
            if (processedEventRepository.existsByEventId(eventId)) {
                LOG.info("Event {} already processed, skipping", eventId);
                return;
            }

            // Extract data from CloudEvents envelope
            JsonNode dataNode = cloudEvent.get("data");
            if (dataNode == null) {
                LOG.error("No data found in CloudEvents envelope");
                return;
            }

            OtpRequestedEvent event = objectMapper.treeToValue(dataNode, OtpRequestedEvent.class);

            // Format and send SMS
            String message = formatOtpMessage(event);
            smsService.sendMessage(event.getPhoneNumber(), message);

            // Mark as processed
            processedEventRepository.save(new ProcessedEvent(eventId, TOPIC));

            LOG.info("Successfully processed OTP request for: {} with purpose: {}", 
                maskPhone(event.getPhoneNumber()), event.getPurpose());

        } catch (Exception e) {
            LOG.error("Error processing otp.requested event: {}", e.getMessage(), e);
            // Don't rethrow - let Kafka handle retries based on configuration
        }
    }

    /**
     * Format OTP message based on purpose and locale.
     */
    private String formatOtpMessage(OtpRequestedEvent event) {
        String locale = event.getLocale() != null ? event.getLocale() : "fr";
        String purpose = event.getPurpose() != null ? event.getPurpose() : "REGISTRATION";

        if ("fr".equals(locale)) {
            return switch (purpose) {
                case "REGISTRATION" -> String.format(
                    "Bienvenue sur OND Money! Votre code de vérification: %s. Valide 5 minutes.",
                    event.getOtpCode()
                );
                case "PIN_RESET" -> String.format(
                    "OND Money: Code de réinitialisation de votre PIN: %s. Valide 5 minutes.",
                    event.getOtpCode()
                );
                case "PIN_CREATION" -> String.format(
                    "OND Money: Code pour créer votre PIN: %s. Valide 5 minutes.",
                    event.getOtpCode()
                );
                case "TRANSACTION_VERIFICATION" -> String.format(
                    "OND Money: Code de vérification de transaction: %s. Valide 5 minutes.",
                    event.getOtpCode()
                );
                default -> String.format(
                    "OND Money: Votre code de vérification: %s. Valide 5 minutes.",
                    event.getOtpCode()
                );
            };
        } else {
            return switch (purpose) {
                case "REGISTRATION" -> String.format(
                    "Welcome to OND Money! Your verification code: %s. Valid for 5 minutes.",
                    event.getOtpCode()
                );
                case "PIN_RESET" -> String.format(
                    "OND Money: Your PIN reset code: %s. Valid for 5 minutes.",
                    event.getOtpCode()
                );
                case "PIN_CREATION" -> String.format(
                    "OND Money: Your PIN creation code: %s. Valid for 5 minutes.",
                    event.getOtpCode()
                );
                case "TRANSACTION_VERIFICATION" -> String.format(
                    "OND Money: Your transaction verification code: %s. Valid for 5 minutes.",
                    event.getOtpCode()
                );
                default -> String.format(
                    "OND Money: Your verification code: %s. Valid for 5 minutes.",
                    event.getOtpCode()
                );
            };
        }
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "****";
        return "****" + phone.substring(phone.length() - 4);
    }
}
