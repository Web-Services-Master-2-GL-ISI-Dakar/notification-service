//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.2
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2025.11.30 à 07:03:46 PM UTC
//

package sn.ondmoney.notification.schemas;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Classe Java pour NotificationType.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="NotificationType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="TRANSACTION_SENT"/&gt;
 *     &lt;enumeration value="TRANSACTION_RECEIVED"/&gt;
 *     &lt;enumeration value="ACCOUNT_CREATED"/&gt;
 *     &lt;enumeration value="PASSWORD_RESET"/&gt;
 *     &lt;enumeration value="LOW_BALANCE"/&gt;
 *     &lt;enumeration value="FAILED_TRANSACTION"/&gt;
 *     &lt;enumeration value="SECURITY_ALERT"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "NotificationType")
@XmlEnum
public enum NotificationType {
    TRANSACTION_SENT,
    TRANSACTION_RECEIVED,
    ACCOUNT_CREATED,
    PASSWORD_RESET,
    LOW_BALANCE,
    FAILED_TRANSACTION,
    SECURITY_ALERT;

    public String value() {
        return name();
    }

    public static NotificationType fromValue(String v) {
        return valueOf(v);
    }
}
