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
 * <p>Classe Java pour NotificationStatus.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * <p>
 * <pre>
 * &lt;simpleType name="NotificationStatus"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="PENDING"/&gt;
 *     &lt;enumeration value="SENT"/&gt;
 *     &lt;enumeration value="FAILED"/&gt;
 *     &lt;enumeration value="RETRY"/&gt;
 *     &lt;enumeration value="CANCELLED"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "NotificationStatus")
@XmlEnum
public enum NotificationStatus {
    PENDING,
    SENT,
    FAILED,
    RETRY,
    CANCELLED;

    public String value() {
        return name();
    }

    public static NotificationStatus fromValue(String v) {
        return valueOf(v);
    }
}
