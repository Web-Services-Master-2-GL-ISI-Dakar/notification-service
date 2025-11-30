package sn.ondmoney.notificationservice.service.handler;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import sn.ondmoney.notificationservice.service.dto.NotificationDTO;

/**
 * Handler pour l'envoi de SMS via l'API Africa's Talking (ou autre provider)
 */
@Component
public class SmsHandler {

    private final Logger log = LoggerFactory.getLogger(SmsHandler.class);

    @Value("${application.notification.sms.api-url}")
    private String apiUrl;

    @Value("${application.notification.sms.api-key}")
    private String apiKey;

    @Value("${application.notification.sms.sender-id}")
    private String senderId;

    @Value("${application.notification.sms.enabled:true}")
    private boolean enabled;

    private final RestTemplate restTemplate;

    public SmsHandler() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Envoie un SMS
     *
     * @param notification La notification contenant le message et le destinataire
     * @return true si l'envoi a réussi, false sinon
     */
    public boolean send(NotificationDTO notification) {
        if (!enabled) {
            log.warn("SMS sending is disabled in configuration");
            return false;
        }

        log.debug("Sending SMS to: {}", notification.getRecipient());

        try {
            // Valider le numéro de téléphone
            String phoneNumber = validatePhoneNumber(notification.getRecipient());
            if (phoneNumber == null) {
                log.error("Invalid phone number: {}", notification.getRecipient());
                return false;
            }

            // Préparer la requête HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apiKey", apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("to", phoneNumber);
            requestBody.put("message", notification.getMessage());
            requestBody.put("from", senderId);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Envoyer le SMS
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl + "/sms/send", request, String.class);

            // Vérifier la réponse
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("SMS sent successfully to: {}", phoneNumber);
                return true;
            } else {
                log.error("SMS sending failed with status: {}", response.getStatusCode());
                return false;
            }
        } catch (Exception e) {
            log.error("Error sending SMS", e);
            return false;
        }
    }

    /**
     * Valide et formate le numéro de téléphone
     * Format attendu : +221XXXXXXXXX pour le Sénégal
     */
    private String validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }

        // Nettoyer le numéro
        String cleaned = phoneNumber.replaceAll("[^0-9+]", "");

        // Ajouter l'indicatif pays si manquant
        if (!cleaned.startsWith("+")) {
            if (cleaned.startsWith("221")) {
                cleaned = "+" + cleaned;
            } else if (cleaned.startsWith("77") || cleaned.startsWith("78") || cleaned.startsWith("70") || cleaned.startsWith("76")) {
                cleaned = "+221" + cleaned;
            }
        }

        // Valider le format
        if (cleaned.matches("\\+221[0-9]{9}")) {
            return cleaned;
        }

        return null;
    }
}
