//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.2
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2025.11.30 à 07:03:46 PM UTC
//

package sn.ondmoney.notification.schemas;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the sn.ondmoney.notification.schemas package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _SendNotificationRequest_QNAME = new QName(
        "http://ondmoney.sn/notification/schemas",
        "SendNotificationRequest"
    );
    private static final QName _SendNotificationResponse_QNAME = new QName(
        "http://ondmoney.sn/notification/schemas",
        "SendNotificationResponse"
    );
    private static final QName _GetNotificationStatusRequest_QNAME = new QName(
        "http://ondmoney.sn/notification/schemas",
        "GetNotificationStatusRequest"
    );
    private static final QName _GetNotificationStatusResponse_QNAME = new QName(
        "http://ondmoney.sn/notification/schemas",
        "GetNotificationStatusResponse"
    );
    private static final QName _GetUserPreferenceRequest_QNAME = new QName(
        "http://ondmoney.sn/notification/schemas",
        "GetUserPreferenceRequest"
    );
    private static final QName _GetUserPreferenceResponse_QNAME = new QName(
        "http://ondmoney.sn/notification/schemas",
        "GetUserPreferenceResponse"
    );
    private static final QName _UpdateUserPreferenceRequest_QNAME = new QName(
        "http://ondmoney.sn/notification/schemas",
        "UpdateUserPreferenceRequest"
    );
    private static final QName _UpdateUserPreferenceResponse_QNAME = new QName(
        "http://ondmoney.sn/notification/schemas",
        "UpdateUserPreferenceResponse"
    );

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: sn.ondmoney.notification.schemas
     *
     */
    public ObjectFactory() {}

    /**
     * Create an instance of {@link SendNotificationRequestType }
     *
     */
    public SendNotificationRequestType createSendNotificationRequestType() {
        return new SendNotificationRequestType();
    }

    /**
     * Create an instance of {@link SendNotificationResponseType }
     *
     */
    public SendNotificationResponseType createSendNotificationResponseType() {
        return new SendNotificationResponseType();
    }

    /**
     * Create an instance of {@link GetNotificationStatusRequestType }
     *
     */
    public GetNotificationStatusRequestType createGetNotificationStatusRequestType() {
        return new GetNotificationStatusRequestType();
    }

    /**
     * Create an instance of {@link GetNotificationStatusResponseType }
     *
     */
    public GetNotificationStatusResponseType createGetNotificationStatusResponseType() {
        return new GetNotificationStatusResponseType();
    }

    /**
     * Create an instance of {@link GetUserPreferenceRequestType }
     *
     */
    public GetUserPreferenceRequestType createGetUserPreferenceRequestType() {
        return new GetUserPreferenceRequestType();
    }

    /**
     * Create an instance of {@link GetUserPreferenceResponseType }
     *
     */
    public GetUserPreferenceResponseType createGetUserPreferenceResponseType() {
        return new GetUserPreferenceResponseType();
    }

    /**
     * Create an instance of {@link UpdateUserPreferenceRequestType }
     *
     */
    public UpdateUserPreferenceRequestType createUpdateUserPreferenceRequestType() {
        return new UpdateUserPreferenceRequestType();
    }

    /**
     * Create an instance of {@link UpdateUserPreferenceResponseType }
     *
     */
    public UpdateUserPreferenceResponseType createUpdateUserPreferenceResponseType() {
        return new UpdateUserPreferenceResponseType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendNotificationRequestType }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link SendNotificationRequestType }{@code >}
     */
    @XmlElementDecl(namespace = "http://ondmoney.sn/notification/schemas", name = "SendNotificationRequest")
    public JAXBElement<SendNotificationRequestType> createSendNotificationRequest(SendNotificationRequestType value) {
        return new JAXBElement<SendNotificationRequestType>(_SendNotificationRequest_QNAME, SendNotificationRequestType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendNotificationResponseType }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link SendNotificationResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://ondmoney.sn/notification/schemas", name = "SendNotificationResponse")
    public JAXBElement<SendNotificationResponseType> createSendNotificationResponse(SendNotificationResponseType value) {
        return new JAXBElement<SendNotificationResponseType>(
            _SendNotificationResponse_QNAME,
            SendNotificationResponseType.class,
            null,
            value
        );
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNotificationStatusRequestType }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetNotificationStatusRequestType }{@code >}
     */
    @XmlElementDecl(namespace = "http://ondmoney.sn/notification/schemas", name = "GetNotificationStatusRequest")
    public JAXBElement<GetNotificationStatusRequestType> createGetNotificationStatusRequest(GetNotificationStatusRequestType value) {
        return new JAXBElement<GetNotificationStatusRequestType>(
            _GetNotificationStatusRequest_QNAME,
            GetNotificationStatusRequestType.class,
            null,
            value
        );
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNotificationStatusResponseType }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetNotificationStatusResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://ondmoney.sn/notification/schemas", name = "GetNotificationStatusResponse")
    public JAXBElement<GetNotificationStatusResponseType> createGetNotificationStatusResponse(GetNotificationStatusResponseType value) {
        return new JAXBElement<GetNotificationStatusResponseType>(
            _GetNotificationStatusResponse_QNAME,
            GetNotificationStatusResponseType.class,
            null,
            value
        );
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUserPreferenceRequestType }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetUserPreferenceRequestType }{@code >}
     */
    @XmlElementDecl(namespace = "http://ondmoney.sn/notification/schemas", name = "GetUserPreferenceRequest")
    public JAXBElement<GetUserPreferenceRequestType> createGetUserPreferenceRequest(GetUserPreferenceRequestType value) {
        return new JAXBElement<GetUserPreferenceRequestType>(
            _GetUserPreferenceRequest_QNAME,
            GetUserPreferenceRequestType.class,
            null,
            value
        );
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUserPreferenceResponseType }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link GetUserPreferenceResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://ondmoney.sn/notification/schemas", name = "GetUserPreferenceResponse")
    public JAXBElement<GetUserPreferenceResponseType> createGetUserPreferenceResponse(GetUserPreferenceResponseType value) {
        return new JAXBElement<GetUserPreferenceResponseType>(
            _GetUserPreferenceResponse_QNAME,
            GetUserPreferenceResponseType.class,
            null,
            value
        );
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateUserPreferenceRequestType }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UpdateUserPreferenceRequestType }{@code >}
     */
    @XmlElementDecl(namespace = "http://ondmoney.sn/notification/schemas", name = "UpdateUserPreferenceRequest")
    public JAXBElement<UpdateUserPreferenceRequestType> createUpdateUserPreferenceRequest(UpdateUserPreferenceRequestType value) {
        return new JAXBElement<UpdateUserPreferenceRequestType>(
            _UpdateUserPreferenceRequest_QNAME,
            UpdateUserPreferenceRequestType.class,
            null,
            value
        );
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateUserPreferenceResponseType }{@code >}
     *
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link UpdateUserPreferenceResponseType }{@code >}
     */
    @XmlElementDecl(namespace = "http://ondmoney.sn/notification/schemas", name = "UpdateUserPreferenceResponse")
    public JAXBElement<UpdateUserPreferenceResponseType> createUpdateUserPreferenceResponse(UpdateUserPreferenceResponseType value) {
        return new JAXBElement<UpdateUserPreferenceResponseType>(
            _UpdateUserPreferenceResponse_QNAME,
            UpdateUserPreferenceResponseType.class,
            null,
            value
        );
    }
}
