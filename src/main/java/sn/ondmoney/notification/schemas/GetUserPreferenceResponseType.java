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
 * <p>Classe Java pour GetUserPreferenceResponseType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="GetUserPreferenceResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="userId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="smsEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="emailEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="pushEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="language" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="mutedTypes" type="{http://ondmoney.sn/notification/schemas}NotificationType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
    name = "GetUserPreferenceResponseType",
    propOrder = { "userId", "smsEnabled", "emailEnabled", "pushEnabled", "language", "mutedTypes" }
)
public class GetUserPreferenceResponseType {

    @XmlElement(required = true)
    protected String userId;

    protected boolean smsEnabled;
    protected boolean emailEnabled;
    protected boolean pushEnabled;

    @XmlElement(required = true)
    protected String language;

    @XmlSchemaType(name = "string")
    protected List<NotificationType> mutedTypes;

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
     * Obtient la valeur de la propriété smsEnabled.
     *
     */
    public boolean isSmsEnabled() {
        return smsEnabled;
    }

    /**
     * Définit la valeur de la propriété smsEnabled.
     *
     */
    public void setSmsEnabled(boolean value) {
        this.smsEnabled = value;
    }

    /**
     * Obtient la valeur de la propriété emailEnabled.
     *
     */
    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    /**
     * Définit la valeur de la propriété emailEnabled.
     *
     */
    public void setEmailEnabled(boolean value) {
        this.emailEnabled = value;
    }

    /**
     * Obtient la valeur de la propriété pushEnabled.
     *
     */
    public boolean isPushEnabled() {
        return pushEnabled;
    }

    /**
     * Définit la valeur de la propriété pushEnabled.
     *
     */
    public void setPushEnabled(boolean value) {
        this.pushEnabled = value;
    }

    /**
     * Obtient la valeur de la propriété language.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Définit la valeur de la propriété language.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * Gets the value of the mutedTypes property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mutedTypes property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMutedTypes().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NotificationType }
     *
     *
     */
    public List<NotificationType> getMutedTypes() {
        if (mutedTypes == null) {
            mutedTypes = new ArrayList<NotificationType>();
        }
        return this.mutedTypes;
    }
}
