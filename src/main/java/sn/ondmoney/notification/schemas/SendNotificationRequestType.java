//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.2
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2025.11.30 à 07:03:46 PM UTC
//

package sn.ondmoney.notification.schemas;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Classe Java pour SendNotificationRequestType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="SendNotificationRequestType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="userId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="accountNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="type" type="{http://ondmoney.sn/notification/schemas}NotificationType"/&gt;
 *         &lt;element name="channels" type="{http://ondmoney.sn/notification/schemas}NotificationChannel" maxOccurs="unbounded"/&gt;
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="priority" type="{http://ondmoney.sn/notification/schemas}Priority"/&gt;
 *         &lt;element name="immediate" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="metadata" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
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
    name = "SendNotificationRequestType",
    propOrder = { "userId", "accountNumber", "type", "channels", "title", "message", "priority", "immediate", "metadata" }
)
public class SendNotificationRequestType {

    @XmlElement(required = true)
    protected String userId;

    protected String accountNumber;

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected NotificationType type;

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected List<NotificationChannel> channels;

    @XmlElement(required = true)
    protected String title;

    @XmlElement(required = true)
    protected String message;

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected Priority priority;

    protected boolean immediate;
    protected String metadata;

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
     * Obtient la valeur de la propriété accountNumber.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Définit la valeur de la propriété accountNumber.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAccountNumber(String value) {
        this.accountNumber = value;
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
     * Gets the value of the channels property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the channels property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChannels().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NotificationChannel }
     *
     *
     */
    public List<NotificationChannel> getChannels() {
        if (channels == null) {
            channels = new ArrayList<NotificationChannel>();
        }
        return this.channels;
    }

    /**
     * Obtient la valeur de la propriété title.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTitle() {
        return title;
    }

    /**
     * Définit la valeur de la propriété title.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTitle(String value) {
        this.title = value;
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
     * Obtient la valeur de la propriété priority.
     *
     * @return
     *     possible object is
     *     {@link Priority }
     *
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Définit la valeur de la propriété priority.
     *
     * @param value
     *     allowed object is
     *     {@link Priority }
     *
     */
    public void setPriority(Priority value) {
        this.priority = value;
    }

    /**
     * Obtient la valeur de la propriété immediate.
     *
     */
    public boolean isImmediate() {
        return immediate;
    }

    /**
     * Définit la valeur de la propriété immediate.
     *
     */
    public void setImmediate(boolean value) {
        this.immediate = value;
    }

    /**
     * Obtient la valeur de la propriété metadata.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMetadata() {
        return metadata;
    }

    /**
     * Définit la valeur de la propriété metadata.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMetadata(String value) {
        this.metadata = value;
    }
}
