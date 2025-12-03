package sn.ondmoney.notification.service.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Modèle de réponse XML de l'API Lafrica Mobile.
 */
@Getter
@Setter
@ToString
@JacksonXmlRootElement(localName = "SendSMSResponse") // Assumer que la racine est SendSMSResponse
public class LafricaSmsResponseDTO {

    // Identifiant unique du message côté opérateur
    @JacksonXmlProperty(localName = "messageId")
    private String messageId;

    // Statut de l'envoi (Ex: 0 = Succès)
    @JacksonXmlProperty(localName = "status")
    private String status;

    // Message de statut détaillé de Lafrica Mobile
    @JacksonXmlProperty(localName = "message")
    private String detailedMessage;
    // Ajoutez ici tout autre champ que l'API Lafrica Mobile retourne (ex: timestamp, credit-remaining)
}
