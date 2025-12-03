package sn.ondmoney.notification.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import sn.ondmoney.notification.service.dto.NotificationPayload;
import sn.ondmoney.notification.service.dto.NotificationTemplateDTO;

@Service
public class TemplateEngineService {

    // Pattern pour trouver les placeholders: {{slotName}}
    private static final Pattern SLOT_PATTERN = Pattern.compile("\\{\\{([a-zA-Z0-9_]+)\\}\\}");

    /**
     * Remplit le corps du template en remplaçant les slots par les valeurs du payload.
     * @param template Le modèle à utiliser.
     * @param payloadData Le conteneur de données brutes.
     * @return Le message final prêt à être envoyé.
     */
    public String fillTemplate(NotificationTemplateDTO template, NotificationPayload payloadData) {
        // 1. Convertir le payload en une Map pour un accès générique
        Map<String, String> dataMap = payloadToMap(payloadData);

        String finalMessage = template.getBodyTemplate();

        // 2. Utilisation de Matcher pour trouver et remplacer tous les slots
        Matcher matcher = SLOT_PATTERN.matcher(finalMessage);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String slotName = matcher.group(1); // slotName est le texte entre {{ et }}
            // Utiliser la dataMap pour récupérer la valeur.
            String replacementValue = dataMap.getOrDefault(slotName, "[VALEUR MANQUANTE]");

            // Assure que la valeur de remplacement est échappée pour le Matcher
            // Matcher.quoteReplacement gère les caractères spéciaux dans la valeur de remplacement
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacementValue));
        }
        matcher.appendTail(buffer); // Ajoute le reste de la chaîne

        return buffer.toString();
    }

    /**
     * Convertit le NotificationPayload DTO en une Map<String, String> pour faciliter
     * le remplissage générique du template. Les valeurs non-String sont converties.
     * Les clés correspondent aux noms des champs dans le NotificationPayload.
     * @param payload Le DTO de données brutes.
     * @return Une Map contenant tous les champs du payload sous forme de String.
     */
    private Map<String, String> payloadToMap(NotificationPayload payload) {
        Map<String, String> dataMap = new HashMap<>();

        // --- Champs Financiers (TXE) ---
        putIfNotNull(dataMap, "transactionId", payload.getTransactionId());
        putIfNotNull(dataMap, "transactionStatus", payload.getTransactionStatus());
        putIfNotNull(dataMap, "transactionType", payload.getTransactionType());
        putIfNotNull(dataMap, "amount", formatAmount(payload.getAmount())); // BigDecimal to String
        putIfNotNull(dataMap, "fees", payload.getFees()); // BigDecimal to String
        putIfNotNull(dataMap, "currency", payload.getCurrency());

        // --- Champs Financiers (BANK2WALLET & WALLET2BANK) ---
        putIfNotNull(dataMap, "bankName", payload.getBankName());
        putIfNotNull(dataMap, "bankAccountNumber", payload.getBankAccountNumber());
        putIfNotNull(dataMap, "bankTransactionCorrelation", payload.getBankTransactionCorrelation());
        putIfNotNull(dataMap, "clientBalance", formatAmount(payload.getClientBalance()));

        // --- Données du Destinateur/Acheteur ---
        putIfNotNull(dataMap, "senderPhone", payload.getSenderPhone());
        putIfNotNull(dataMap, "senderName", payload.getSenderName());
        putIfNotNull(dataMap, "senderBalance", formatAmount(payload.getSenderBalance())); // BigDecimal to String

        // --- Données du Receveur/Bénéficiaire/Marchand ---
        putIfNotNull(dataMap, "receiverPhone", payload.getReceiverPhone());
        putIfNotNull(dataMap, "receiverName", payload.getReceiverName());
        putIfNotNull(dataMap, "receiverBalance", formatAmount(payload.getReceiverBalance())); // BigDecimal to String

        // --- Temps et Référence ---
        putIfNotNull(dataMap, "transactionDate", formatDate(payload.getTransactionDate()));
        putIfNotNull(dataMap, "commandRef", payload.getCommandRef());

        // --- Champs d'Authentification (Auth) ---
        putIfNotNull(dataMap, "verificationCode", payload.getVerificationCode());
        putIfNotNull(dataMap, "verificationCodeExpiryInMinutes", payload.getVerificationCodeExpiryInMinutes()); // Integer to String
        putIfNotNull(dataMap, "ipAddress", payload.getIpAddress());
        putIfNotNull(dataMap, "deviceId", payload.getDeviceId());

        // --- Données Additionnelles ---
        if (payload.getAdditionalData() != null) {
            dataMap.putAll(payload.getAdditionalData());
        }

        return dataMap;
    }

    /** Helper pour ajouter des valeurs à la map si elles ne sont pas nulles. */
    private void putIfNotNull(Map<String, String> map, String key, Object value) {
        if (value != null) {
            map.put(key, value.toString());
        }
    }

    /// 01/12/2025 à 10h40
    private String formatDate(Instant date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH'h'mm", Locale.FRENCH);
        return date.atZone(ZoneId.systemDefault()).format(formatter);
    }

    /// 5 000 000 F
    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return null;
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRENCH);
        symbols.setGroupingSeparator(' ');

        DecimalFormat formatter = new DecimalFormat("#,##0", symbols);

        String formattedAmount = formatter.format(amount);

        return formattedAmount + " F";
    }
}
