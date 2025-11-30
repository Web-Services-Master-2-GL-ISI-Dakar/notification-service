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
 * <p>Classe Java pour GetNotificationStatusResponseType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="GetNotificationStatusResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="notificationId" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="userId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="type" type="{http://ondmoney.sn/notification/schemas}NotificationType"/&gt;
 *         &lt;element name="status" type="{http://ondmoney.sn/notification/schemas}NotificationStatus"/&gt;
 *         &lt;element name="sentAt" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="errorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="retryCount" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "GetNotificationStatusResponseType",
    propOrder = { "notificationId", "userId", "type", "status", "sentAt", "errorMessage", "retryCount" }
)
public class GetNotificationStatusResponseType {

    protected long notificationId;

    @XmlElement(required = true)
    protected String userId;

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected NotificationType type;

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected NotificationStatus status;

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar sentAt;

    protected String errorMessage;
    protected int retryCount;

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
     * Obtient la valeur de la propriété userId.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Définit la valeur de la propriété userId.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUserId(String value) {
        this.userId = value;
    }

    /**
     * Obtient la valeur de la propriété type.
     *
     * @return
     *     possible object is
     *     {@link NotificationType }
     *
     */
    public NotificationType getType() {
        return type;
    }

    /**
     * Définit la valeur de la propriété type.
     *
     * @param value
     *     allowed object is
     *     {@link NotificationType }
     *
     */
    public void setType(NotificationType value) {
        this.type = value;
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
     * Obtient la valeur de la propriété sentAt.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getSentAt() {
        return sentAt;
    }

    /**
     * Définit la valeur de la propriété sentAt.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setSentAt(XMLGregorianCalendar value) {
        this.sentAt = value;
    }

    /**
     * Obtient la valeur de la propriété errorMessage.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Définit la valeur de la propriété errorMessage.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setErrorMessage(String value) {
        this.errorMessage = value;
    }

    /**
     * Obtient la valeur de la propriété retryCount.
     *
     */
    public int getRetryCount() {
        return retryCount;
    }

    /**
     * Définit la valeur de la propriété retryCount.
     *
     */
    public void setRetryCount(int value) {
        this.retryCount = value;
    }
}
