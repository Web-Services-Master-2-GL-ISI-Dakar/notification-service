//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.2
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2025.11.30 à 07:03:46 PM UTC
//

package sn.ondmoney.notification.schemas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>Classe Java pour SendNotificationResponseType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="SendNotificationResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="notificationId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="status" type="{http://ondmoney.sn/notification/schemas}NotificationStatus"/&gt;
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SendNotificationResponseType", propOrder = { "notificationId", "status", "message", "timestamp" })
public class SendNotificationResponseType {

    protected long notificationId;

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected NotificationStatus status;

    @XmlElement(required = true)
    protected String message;

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;

    /**
     * Obtient la valeur de la propriété notificationId.
     *
     */
    public long getNotificationId() {
        return notificationId;
    }

    /**
     * Définit la valeur de la propriété notificationId.
     *
     */
    public void setNotificationId(long value) {
        this.notificationId = value;
    }

    /**
     * Obtient la valeur de la propriété status.
     *
     * @return
     *     possible object is
     *     {@link NotificationStatus }
     *
     */
    public NotificationStatus getStatus() {
        return status;
    }

    /**
     * Définit la valeur de la propriété status.
     *
     * @param value
     *     allowed object is
     *     {@link NotificationStatus }
     *
     */
    public void setStatus(NotificationStatus value) {
        this.status = value;
    }

    /**
     * Obtient la valeur de la propriété message.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMessage() {
        return message;
    }

    /**
     * Définit la valeur de la propriété message.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Obtient la valeur de la propriété timestamp.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Définit la valeur de la propriété timestamp.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }
}
