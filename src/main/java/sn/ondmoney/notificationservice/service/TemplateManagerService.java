package sn.ondmoney.notificationservice.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationChannel;
import sn.ondmoney.notificationservice.domain.enumeration.NotificationType;
import sn.ondmoney.notificationservice.service.dto.NotificationTemplateDTO;

/**
 * Service de gestion des templates de notification
 *
 * Ce service :
 * - Récupère les templates depuis la base de données
 * - Remplace les variables dans les templates
 * - Formate les messages selon le canal (SMS, Email, Push)
 */
@Service
public class TemplateManagerService {

    private final Logger log = LoggerFactory.getLogger(TemplateManagerService.class);
    private final NotificationTemplateService templateService;

    // Formatter pour les dates
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(
        ZoneId.systemDefault()
    );

    public TemplateManagerService(NotificationTemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * Génère le message à partir d'un template
     *
     * @param type Type de notification
     * @param channel Canal de notification
     * @param data Données à injecter dans le template
     * @return Message formaté
     */
    public String renderTemplate(NotificationType type, NotificationChannel channel, Map<String, Object> data) {
        log.debug("Rendering template for type: {}, channel: {}", type, channel);

        try {
            // 1. Chercher le template en base de données
            Optional<NotificationTemplateDTO> templateOpt = templateService.findByTypeAndLanguage(type, "fr"); // TODO: Utiliser la langue de l'utilisateur

            String template;
            if (templateOpt.isPresent()) {
                NotificationTemplateDTO templateDTO = templateOpt.get();

                // Sélectionner le bon template selon le canal
                switch (channel) {
                    case SMS:
                        template = templateDTO.getSmsTemplate();
                        break;
                    case EMAIL:
                        template = templateDTO.getBodyTemplate();
                        break;
                    case PUSH:
                        template = templateDTO.getPushBody();
                        break;
                    default:
                        template = templateDTO.getBodyTemplate();
                }
            } else {
                // Template par défaut si aucun n'est trouvé en base
                template = getDefaultTemplate(type, channel);
            }

            // 2. Remplacer les variables dans le template
            return replacePlaceholders(template, data);
        } catch (Exception e) {
            log.error("Error rendering template", e);
            return getErrorMessage(type);
        }
    }

    /**
     * Récupère le titre de la notification
     */
    public String getTitle(NotificationType type, Map<String, Object> data) {
        Optional<NotificationTemplateDTO> templateOpt = templateService.findByTypeAndLanguage(type, "fr");

        if (templateOpt.isPresent()) {
            String subject = templateOpt.get().getSubject();
            return replacePlaceholders(subject, data);
        }

        return getDefaultTitle(type);
    }

    /**
     * Remplace les placeholders dans le template par les vraies valeurs
     *
     * Exemple : "Vous avez reçu {amount} {currency}"
     *        -> "Vous avez reçu 5000 FCFA"
     */
    private String replacePlaceholders(String template, Map<String, Object> data) {
        String result = template;

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = formatValue(entry.getValue());
            result = result.replace(placeholder, value);
        }

        return result;
    }

    /**
     * Formate une valeur selon son type
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof BigDecimal) {
            BigDecimal decimal = (BigDecimal) value;
            return String.format("%,.0f", decimal);
        }

        if (value instanceof Instant) {
            Instant instant = (Instant) value;
            return DATE_FORMATTER.format(instant);
        }

        return value.toString();
    }

    /**
     * Templates par défaut (fallback si pas en base de données)
     */
    private String getDefaultTemplate(NotificationType type, NotificationChannel channel) {
        String template = "";

        switch (type) {
            case TRANSACTION_SENT:
                if (channel == NotificationChannel.SMS) {
                    template = "Transaction effectuée. Vous avez envoyé {amount} {currency} à {recipient}. ID: {transactionId}";
                } else {
                    template =
                        "Votre transaction a été effectuée avec succès.\n\n" +
                        "Montant: {amount} {currency}\n" +
                        "Destinataire: {recipient}\n" +
                        "Date: {timestamp}\n" +
                        "Référence: {transactionId}";
                }
                break;
            case TRANSACTION_RECEIVED:
                if (channel == NotificationChannel.SMS) {
                    template = "Vous avez reçu {amount} {currency} de {sender}. ID: {transactionId}";
                } else {
                    template =
                        "Vous avez reçu un paiement.\n\n" +
                        "Montant: {amount} {currency}\n" +
                        "Expéditeur: {sender}\n" +
                        "Date: {timestamp}\n" +
                        "Référence: {transactionId}";
                }
                break;
            case ACCOUNT_CREATED:
                template = "Bienvenue ! Votre compte OndMoney a été créé avec succès. " + "Numéro de compte: {accountNumber}";
                break;
            case SECURITY_ALERT:
                template = "ALERTE SÉCURITÉ: {description}. " + "Si ce n'est pas vous, contactez immédiatement le support.";
                break;
            case LOW_BALANCE:
                template = "Votre solde est faible: {balance} {currency}. " + "Pensez à recharger votre compte.";
                break;
            case FAILED_TRANSACTION:
                template = "Transaction échouée. Montant: {amount} {currency}. " + "Raison: {reason}. ID: {transactionId}";
                break;
            default:
                template = "Notification OndMoney";
        }

        return template;
    }

    /**
     * Titres par défaut
     */
    private String getDefaultTitle(NotificationType type) {
        switch (type) {
            case TRANSACTION_SENT:
                return "Transaction envoyée";
            case TRANSACTION_RECEIVED:
                return "Paiement reçu";
            case ACCOUNT_CREATED:
                return "Compte créé";
            case SECURITY_ALERT:
                return "Alerte de sécurité";
            case LOW_BALANCE:
                return "Solde faible";
            case FAILED_TRANSACTION:
                return "Transaction échouée";
            default:
                return "Notification OndMoney";
        }
    }

    /**
     * Message d'erreur si le rendering échoue
     */
    private String getErrorMessage(NotificationType type) {
        return "Une notification a été générée. Type: " + type.toString();
    }
}
