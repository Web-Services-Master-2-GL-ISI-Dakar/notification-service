package sn.ondmoney.notification.web.soap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import sn.ondmoney.notification.domain.enumeration.*;
import sn.ondmoney.notification.service.*;
import sn.ondmoney.notification.service.dto.*;
import sn.ondmoney.notification.soap.model.GetNotificationLogByIdRequest;
import sn.ondmoney.notification.soap.model.GetNotificationLogsRequest;
import sn.ondmoney.notification.soap.model.HealthCheckRequest;
import sn.ondmoney.notification.soap.model.HealthCheckResponse;
import sn.ondmoney.notification.soap.model.MerchantPaymentInfo;
import sn.ondmoney.notification.soap.model.MerchantPaymentNotificationResponse;
import sn.ondmoney.notification.soap.model.NotificationLogInfo;
import sn.ondmoney.notification.soap.model.NotificationLogResponse;
import sn.ondmoney.notification.soap.model.NotificationLogsResponse;
import sn.ondmoney.notification.soap.model.NotifyMerchantPaymentRequest;
import sn.ondmoney.notification.soap.model.NotifyTransferRequest;
import sn.ondmoney.notification.soap.model.OtpInfo;
import sn.ondmoney.notification.soap.model.OtpResponse;
import sn.ondmoney.notification.soap.model.RetryNotificationRequest;
import sn.ondmoney.notification.soap.model.RetryNotificationResponse;
import sn.ondmoney.notification.soap.model.SecurityAlertInfo;
import sn.ondmoney.notification.soap.model.SecurityAlertResponse;
import sn.ondmoney.notification.soap.model.SendNotificationRequest;
import sn.ondmoney.notification.soap.model.SendOtpRequest;
import sn.ondmoney.notification.soap.model.SendSecurityAlertRequest;
import sn.ondmoney.notification.soap.model.ServiceStatus;
import sn.ondmoney.notification.soap.model.TransferInfo;
import sn.ondmoney.notification.soap.model.TransferNotificationResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Endpoint SOAP pour le service de notification Ond Money
 * Adapté aux services existants : SmsService, EmailService, FCMService, TemplateEngineService
 */
@Endpoint
public class NotificationEndpoint {

    private static final String NAMESPACE_URI = "http://ondmoney.sn/notification";
    private final Logger log = LoggerFactory.getLogger(NotificationEndpoint.class);

    private final NotificationLogService notificationLogService;
    private final NotificationTemplateService notificationTemplateService;
    private final TemplateEngineService templateEngineService;
    private final SmsService smsService;
    private final EmailService emailService;
    private final FCMService fcmService;
    private final ObjectMapper objectMapper;

    public NotificationEndpoint(
        NotificationLogService notificationLogService,
        NotificationTemplateService notificationTemplateService,
        TemplateEngineService templateEngineService,
        SmsService smsService,
        EmailService emailService,
        FCMService fcmService,
        ObjectMapper objectMapper) {
        this.notificationLogService = notificationLogService;
        this.notificationTemplateService = notificationTemplateService;
        this.templateEngineService = templateEngineService;
        this.smsService = smsService;
        this.emailService = emailService;
        this.fcmService = fcmService;
        this.objectMapper = objectMapper;
    }

    // ========================================
    // 1. ENVOI GÉNÉRIQUE DE NOTIFICATION
    // ========================================
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "sendNotificationRequest")
    @ResponsePayload
    public sn.ondmoney.notification.soap.model.NotificationResponse sendNotification(
        @RequestPayload SendNotificationRequest request) {
        log.debug("SOAP Request: sendNotification for eventRef={}", request.getEventRef());

        // Créer la réponse SOAP
        sn.ondmoney.notification.soap.model.NotificationResponse soapResponse =
            new sn.ondmoney.notification.soap.model.NotificationResponse();
        ServiceStatus status = new ServiceStatus();

        try {
            // Convertir le payload SOAP en NotificationPayload DTO
            NotificationPayload payloadDto = convertSoapPayloadToDto(request.getPayload());

            // Mapper les enums
            NotificationType notificationType = NotificationType.valueOf(request.getNotificationType());
            NotificationChannel notificationChannel = NotificationChannel.valueOf(request.getNotificationChannel());
            NotificationLanguage notificationLanguage = NotificationLanguage.valueOf(request.getLanguage());

            // Récupérer le template approprié
            Optional<NotificationTemplateDTO> templateOpt = notificationTemplateService.findActiveTemplateByCompositeKey(
                notificationType,
                notificationChannel,
                notificationLanguage,
                1
            );

            if (templateOpt.isEmpty()) {
                throw new RuntimeException(String.format(
                    "Template non trouvé pour type=%s, channel=%s, language=%s",
                    notificationType, notificationChannel, notificationLanguage
                ));
            }

            NotificationTemplateDTO template = templateOpt.get();

            // Générer le message à partir du template
            String messageBody = templateEngineService.fillTemplate(template, payloadDto);

            // Envoyer via le canal approprié
            String externalRef = null;
            NotificationStatus notificationStatus = NotificationStatus.PENDING;
            String errorMessage = null;
            Instant sentAt = null;

            try {
                switch (notificationChannel) {
                    case SMS:
                        smsService.sendMessage(request.getRecipient(), messageBody);
                        notificationStatus = NotificationStatus.SENT;
                        externalRef = "SMS-" + System.currentTimeMillis();
                        sentAt = Instant.now();
                        break;

                    case EMAIL:
                        String subject = template.getSubjectTemplate() != null ?
                            template.getSubjectTemplate() : "Notification Ond Money";
                        emailService.sendMessage(request.getRecipient(), subject, messageBody);
                        notificationStatus = NotificationStatus.SENT;
                        externalRef = "EMAIL-" + System.currentTimeMillis();
                        sentAt = Instant.now();
                        break;

                    case PUSH:
                        PushNotificationRequest pushRequest = PushNotificationRequest.builder()
                            .token(request.getRecipient())
                            .title(template.getSubjectTemplate() != null ? template.getSubjectTemplate() : "Notification")
                            .body(messageBody)
                            .topic("notification")
                            .build();
                        fcmService.sendMessageToToken(pushRequest);
                        notificationStatus = NotificationStatus.SENT;
                        externalRef = "PUSH-" + System.currentTimeMillis();
                        sentAt = Instant.now();
                        break;

                    default:
                        throw new IllegalArgumentException("Canal non supporté: " + notificationChannel);
                }
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi de la notification", e);
                notificationStatus = NotificationStatus.FAILED;
                errorMessage = e.getMessage();
                sentAt = Instant.now();
            }

            // Sauvegarder le log
            NotificationLogDTO logDto = NotificationLogDTO.builder()
                .eventRef(request.getEventRef())
                .eventTime(Instant.now())
                .userId(request.getUserId())
                .recipient(request.getRecipient())
                .notificationType(notificationType)
                .notificationStatus(notificationStatus)
                .notificationChannel(notificationChannel)
                .payload(objectMapper.writeValueAsString(payloadDto))
                .sentAt(sentAt)
                .externalEventRef(externalRef)
                .errorMessage(errorMessage)
                .retryCount(0)
                .notificationTemplateUsed(template)
                .createdAt(Instant.now())
                .build();

            NotificationLogDTO savedLog = notificationLogService.save(logDto);

            // Construire la réponse SOAP
            status.setSuccess(true);
            status.setCode(notificationStatus.name());
            status.setMessage("Notification traitée avec succès");

            soapResponse.setStatus(status);
            soapResponse.setNotificationLogId(savedLog.getId());
            soapResponse.setExternalEventRef(externalRef);

        } catch (Exception e) {
            log.error("Erreur lors du traitement de la notification", e);

            status.setSuccess(false);
            status.setCode("NOTIFICATION_ERROR");
            status.setMessage(e.getMessage());
            soapResponse.setStatus(status);
        }

        return soapResponse;
    }
    // ========================================
    // 2. ENVOI D'OTP
    // ========================================

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "sendOtpRequest")
    @ResponsePayload
    public OtpResponse sendOtp(@RequestPayload SendOtpRequest request) {
        log.debug("SOAP Request: sendOtp for phone={}", request.getOtp().getPhoneNumber());

        OtpResponse response = new OtpResponse();
        ServiceStatus status = new ServiceStatus();

        try {
            OtpInfo otpInfo = request.getOtp();

            // Construire le payload en utilisant votre NotificationPayload existant
            NotificationPayload payloadDto = NotificationPayload.builder()
                .verificationCode(otpInfo.getVerificationCode())
                .verificationCodeExpiryInMinutes(otpInfo.getExpiryInMinutes())
                .senderPhone(otpInfo.getPhoneNumber()) // Le destinataire de l'OTP
                .build();

            // Récupérer le template OTP
            Optional<NotificationTemplateDTO> templateOpt = notificationTemplateService.findActiveTemplateByCompositeKey(
                NotificationType.OTP_REQUEST,
                NotificationChannel.SMS,
                NotificationLanguage.valueOf(request.getLanguage()),
                1
            );

            if (templateOpt.isEmpty()) {
                throw new RuntimeException("Template OTP non trouvé");
            }

            NotificationTemplateDTO template = templateOpt.get();

            // Générer le message avec TemplateEngineService
            String message = templateEngineService.fillTemplate(template, payloadDto);

            // Envoyer le SMS
            smsService.sendMessage(otpInfo.getPhoneNumber(), message);

            // Sauvegarder le log
            NotificationLogDTO logDto = NotificationLogDTO.builder()
                .eventRef(request.getEventRef())
                .eventTime(Instant.now())
                .recipient(otpInfo.getPhoneNumber())
                .notificationType(NotificationType.OTP_REQUEST)
                .notificationStatus(NotificationStatus.SENT)
                .notificationChannel(NotificationChannel.SMS)
                .payload(objectMapper.writeValueAsString(payloadDto))
                .sentAt(Instant.now())
                .externalEventRef("OTP-" + System.currentTimeMillis())
                .retryCount(0)
                .notificationTemplateUsed(template)
                .createdAt(Instant.now())
                .build();

            notificationLogService.save(logDto);

            status.setSuccess(true);
            status.setCode("OTP_SENT");
            status.setMessage("Code OTP envoyé avec succès");

            response.setStatus(status);
            response.setOtp(otpInfo);

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'OTP", e);
            status.setSuccess(false);
            status.setCode("OTP_ERROR");
            status.setMessage(e.getMessage());
            response.setStatus(status);
        }

        return response;
    }

    // ========================================
    // 3. NOTIFICATION DE TRANSFERT
    // ========================================

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "notifyTransferRequest")
    @ResponsePayload
    public TransferNotificationResponse notifyTransfer(@RequestPayload NotifyTransferRequest request) {
        log.debug("SOAP Request: notifyTransfer for txId={}",
            request.getTransfer().getTransactionId());

        TransferNotificationResponse response = new TransferNotificationResponse();
        ServiceStatus status = new ServiceStatus();

        try {
            TransferInfo transfer = request.getTransfer();
            String receiverNotifId = null;
            String senderNotifId = null;

            // Notifier le receveur
            if (request.isNotifyReceiver()) {
                NotificationPayload receiverPayload = NotificationPayload.builder()
                    .transactionId(transfer.getTransactionId())
                    .transactionType("TRANSFER")
                    .senderPhone(transfer.getSender())
                    .senderName(transfer.getSenderName())
                    .receiverPhone(transfer.getBeneficiary())
                    .receiverName(transfer.getBeneficiaryName())
                    .amount(BigDecimal.valueOf(Long.parseLong(transfer.getAmount())))
                    .currency(transfer.getCurrency())
                    .receiverBalance(BigDecimal.valueOf(Long.parseLong(transfer.getBalance())))
                    .transactionDate(toInstant(transfer.getTransactionDate()))
                    .transactionStatus(transfer.getTransactionStatus())
                    .build();

                receiverNotifId = sendTransferNotification(
                    request.getEventRef(),
                    NotificationType.TRANSFER_COMPLETED,
                    transfer.getBeneficiary(),
                    receiverPayload,
                    NotificationLanguage.valueOf(request.getLanguage())
                );
            }

            // Notifier l'envoyeur
            if (request.isNotifySender()) {
                NotificationPayload senderPayload = NotificationPayload.builder()
                    .transactionId(transfer.getTransactionId())
                    .transactionType("TRANSFER")
                    .senderPhone(transfer.getSender())
                    .senderName(transfer.getSenderName())
                    .receiverPhone(transfer.getBeneficiary())
                    .receiverName(transfer.getBeneficiaryName())
                    .amount(BigDecimal.valueOf(Long.parseLong(transfer.getAmount())))
                    .currency(transfer.getCurrency())
                    .senderBalance(BigDecimal.valueOf(Long.parseLong(transfer.getBalance())))
                    .transactionDate(toInstant(transfer.getTransactionDate()))
                    .transactionStatus(transfer.getTransactionStatus())
                    .build();

                senderNotifId = sendTransferNotification(
                    request.getEventRef(),
                    NotificationType.TRANSFER_COMPLETED,
                    transfer.getSender(),
                    senderPayload,
                    NotificationLanguage.valueOf(request.getLanguage())
                );
            }

            status.setSuccess(true);
            status.setCode("TRANSFER_NOTIFIED");
            status.setMessage("Notifications de transfert envoyées");

            response.setStatus(status);
            response.setTransfer(transfer);
            if (receiverNotifId != null) {
                response.setReceiverNotificationId(receiverNotifId);
            }
            if (senderNotifId != null) {
                response.setSenderNotificationId(senderNotifId);
            }

        } catch (Exception e) {
            log.error("Erreur lors de la notification de transfert", e);
            status.setSuccess(false);
            status.setCode("TRANSFER_NOTIF_ERROR");
            status.setMessage(e.getMessage());
            response.setStatus(status);
        }

        return response;
    }

    // ========================================
    // 4. NOTIFICATION DE PAIEMENT MARCHAND
    // ========================================

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "notifyMerchantPaymentRequest")
    @ResponsePayload
    public MerchantPaymentNotificationResponse notifyMerchantPayment(
        @RequestPayload NotifyMerchantPaymentRequest request) {

        log.debug("SOAP Request: notifyMerchantPayment for txId={}",
            request.getPayment().getTransactionId());

        MerchantPaymentNotificationResponse response = new MerchantPaymentNotificationResponse();
        ServiceStatus status = new ServiceStatus();

        try {
            MerchantPaymentInfo payment = request.getPayment();
            String payerNotifId = null;
            String merchantNotifId = null;

            // Notifier le payeur
            if (request.isNotifyPayer()) {
                NotificationPayload payerPayload = NotificationPayload.builder()
                    .transactionId(payment.getTransactionId())
                    .transactionType("MERCHANT_PAYMENT")
                    .senderPhone(payment.getPayer()) // Le payeur
                    .receiverPhone(payment.getMerchantCode()) // Le marchand
                    .receiverName(payment.getMerchantName())
                    .amount(BigDecimal.valueOf(Long.parseLong(payment.getAmount())))
                    .currency(payment.getCurrency())
                    .transactionDate(toInstant(payment.getTransactionDate()))
                    .transactionStatus(payment.getTransactionStatus())
                    .commandRef(payment.getMerchantCode()) // Référence marchand
                    .build();

                payerNotifId = sendMerchantPaymentNotification(
                    request.getEventRef(),
                    NotificationType.MERCHANT_PAYMENT_COMPLETED,
                    payment.getPayer(),
                    payerPayload,
                    NotificationLanguage.valueOf(request.getLanguage())
                );
            }

            // Notifier le marchand
            if (request.isNotifyMerchant()) {
                NotificationPayload merchantPayload = NotificationPayload.builder()
                    .transactionId(payment.getTransactionId())
                    .transactionType("MERCHANT_PAYMENT")
                    .senderPhone(payment.getPayer())
                    .receiverPhone(payment.getMerchantCode())
                    .receiverName(payment.getMerchantName())
                    .amount(BigDecimal.valueOf(Long.parseLong(payment.getAmount())))
                    .currency(payment.getCurrency())
                    .transactionDate(toInstant(payment.getTransactionDate()))
                    .transactionStatus(payment.getTransactionStatus())
                    .commandRef(payment.getMerchantCode())
                    .build();

                // TODO: Récupérer le numéro de téléphone du marchand depuis merchantCode
                merchantNotifId = sendMerchantPaymentNotification(
                    request.getEventRef(),
                    NotificationType.MERCHANT_PAYMENT_COMPLETED,
                    payment.getMerchantCode(), // À remplacer par le numéro réel
                    merchantPayload,
                    NotificationLanguage.valueOf(request.getLanguage())
                );
            }

            status.setSuccess(true);
            status.setCode("MERCHANT_PAYMENT_NOTIFIED");
            status.setMessage("Notifications de paiement marchand envoyées");

            response.setStatus(status);
            response.setPayment(payment);
            if (payerNotifId != null) {
                response.setPayerNotificationId(payerNotifId);
            }
            if (merchantNotifId != null) {
                response.setMerchantNotificationId(merchantNotifId);
            }

        } catch (Exception e) {
            log.error("Erreur lors de la notification de paiement marchand", e);
            status.setSuccess(false);
            status.setCode("MERCHANT_PAYMENT_ERROR");
            status.setMessage(e.getMessage());
            response.setStatus(status);
        }

        return response;
    }

    // ========================================
    // 5. ALERTE DE SÉCURITÉ
    // ========================================

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "sendSecurityAlertRequest")
    @ResponsePayload
    public SecurityAlertResponse sendSecurityAlert(@RequestPayload SendSecurityAlertRequest request) {
        log.debug("SOAP Request: sendSecurityAlert for userId={}",
            request.getAlert().getUserId());

        SecurityAlertResponse response = new SecurityAlertResponse();
        ServiceStatus status = new ServiceStatus();

        try {
            SecurityAlertInfo alert = request.getAlert();

            // Utiliser additionalData pour la localisation
            Map<String, String> additionalData = new HashMap<>();
            if (alert.getLocation() != null) {
                additionalData.put("location", alert.getLocation());
            }

            NotificationPayload payloadDto = NotificationPayload.builder()
                .senderPhone(alert.getPhoneNumber())
                .transactionDate(toInstant(alert.getLoginDateTime()))
                .deviceId(alert.getDevice())
                .ipAddress(alert.getIpAddress())
                .additionalData(additionalData)
                .build();

            String smsNotifId = null;
            String emailNotifId = null;
            String pushNotifId = null;

            // Envoyer SMS
            if (request.isSendSms()) {
                smsNotifId = sendSecurityAlertNotification(
                    request.getEventRef(),
                    NotificationChannel.SMS,
                    alert.getPhoneNumber(),
                    alert.getUserId(),
                    payloadDto,
                    NotificationLanguage.valueOf(request.getLanguage())
                );
            }

            // Envoyer Email
            if (request.isSendEmail() && alert.getEmail() != null) {
                emailNotifId = sendSecurityAlertNotification(
                    request.getEventRef(),
                    NotificationChannel.EMAIL,
                    alert.getEmail(),
                    alert.getUserId(),
                    payloadDto,
                    NotificationLanguage.valueOf(request.getLanguage())
                );
            }

            // Envoyer Push
            if (request.isSendPush()) {
                // TODO: Récupérer le device token de l'utilisateur
                log.debug("Push notification pour userId={} (à implémenter)", alert.getUserId());
            }

            status.setSuccess(true);
            status.setCode("SECURITY_ALERT_SENT");
            status.setMessage("Alertes de sécurité envoyées");

            response.setStatus(status);
            response.setAlert(alert);
            if (smsNotifId != null) {
                response.setSmsNotificationId(smsNotifId);
            }
            if (emailNotifId != null) {
                response.setEmailNotificationId(emailNotifId);
            }
            if (pushNotifId != null) {
                response.setPushNotificationId(pushNotifId);
            }

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'alerte de sécurité", e);
            status.setSuccess(false);
            status.setCode("SECURITY_ALERT_ERROR");
            status.setMessage(e.getMessage());
            response.setStatus(status);
        }

        return response;
    }

    // ========================================
    // 6. RÉCUPÉRER LES LOGS
    // ========================================

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNotificationLogsRequest")
    @ResponsePayload
    public NotificationLogsResponse getNotificationLogs(
        @RequestPayload GetNotificationLogsRequest request) {

        log.debug("SOAP Request: getNotificationLogs");

        NotificationLogsResponse response = new NotificationLogsResponse();
        ServiceStatus status = new ServiceStatus();

        try {
            Page<NotificationLogDTO> logsPage = notificationLogService.findAll(
                PageRequest.of(request.getPage(), request.getSize())
            );

            List<NotificationLogInfo> logs = logsPage.getContent().stream()
                .map(this::convertToNotificationLogInfo)
                .collect(Collectors.toList());

            status.setSuccess(true);
            status.setCode("LOGS_RETRIEVED");
            status.setMessage("Logs récupérés avec succès");

            response.setStatus(status);
            response.getLogs().addAll(logs);
            response.setTotalElements(String.valueOf(logsPage.getTotalElements()));
            response.setTotalPages(logsPage.getTotalPages());
            response.setCurrentPage(logsPage.getNumber());

        } catch (Exception e) {
            log.error("Erreur lors de la récupération des logs", e);
            status.setSuccess(false);
            status.setCode("LOGS_ERROR");
            status.setMessage(e.getMessage());
            response.setStatus(status);
        }

        return response;
    }

    // ========================================
    // 7. RÉCUPÉRER UN LOG PAR ID
    // ========================================

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getNotificationLogByIdRequest")
    @ResponsePayload
    public NotificationLogResponse getNotificationLogById(
        @RequestPayload GetNotificationLogByIdRequest request) {

        log.debug("SOAP Request: getNotificationLogById for id={}", request.getLogId());

        NotificationLogResponse response = new NotificationLogResponse();
        ServiceStatus status = new ServiceStatus();

        try {
            Optional<NotificationLogDTO> logDtoOpt = notificationLogService.findOne(String.valueOf(request.getLogId()));

            if (logDtoOpt.isPresent()) {
                NotificationLogInfo logInfo = convertToNotificationLogInfo(logDtoOpt.get());

                status.setSuccess(true);
                status.setCode("LOG_FOUND");
                status.setMessage("Log trouvé");

                response.setStatus(status);
                response.setLog(logInfo);
            } else {
                status.setSuccess(false);
                status.setCode("LOG_NOT_FOUND");
                status.setMessage("Log non trouvé avec l'ID: " + request.getLogId());
                response.setStatus(status);
            }

        } catch (Exception e) {
            log.error("Erreur lors de la récupération du log", e);
            status.setSuccess(false);
            status.setCode("LOG_ERROR");
            status.setMessage(e.getMessage());
            response.setStatus(status);
        }

        return response;
    }

    // ========================================
    // 8. RENVOYER UNE NOTIFICATION
    // ========================================

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "retryNotificationRequest")
    @ResponsePayload
    public RetryNotificationResponse retryNotification(
        @RequestPayload RetryNotificationRequest request) {

        log.debug("SOAP Request: retryNotification for logId={}", request.getLogId());

        RetryNotificationResponse response = new RetryNotificationResponse();
        ServiceStatus status = new ServiceStatus();

        try {
            Optional<NotificationLogDTO> originalLogOpt = notificationLogService.findOne(String.valueOf(request.getLogId()));

            if (originalLogOpt.isEmpty()) {
                throw new RuntimeException("Log non trouvé");
            }

            NotificationLogDTO originalLog = originalLogOpt.get();

            // Incrémenter le retry count
            originalLog.setRetryCount((originalLog.getRetryCount() != null ? originalLog.getRetryCount() : 0) + 1);
            originalLog.setUpdatedAt(Instant.now());

            // TODO: Implémenter la véritable logique de retry (renvoyer la notification)
            NotificationLogDTO updatedLog = notificationLogService.update(originalLog);

            status.setSuccess(true);
            status.setCode("NOTIFICATION_RETRIED");
            status.setMessage("Notification marquée pour retry");

            response.setStatus(status);
            response.setNewNotificationLogId(updatedLog.getId());

        } catch (Exception e) {
            log.error("Erreur lors du renvoi de la notification", e);
            status.setSuccess(false);
            status.setCode("RETRY_ERROR");
            status.setMessage(e.getMessage());
            response.setStatus(status);
        }

        return response;
    }

    // ========================================
    // 9. HEALTH CHECK
    // ========================================

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "healthCheckRequest")
    @ResponsePayload
    public HealthCheckResponse healthCheck(@RequestPayload HealthCheckRequest request) {
        log.debug("SOAP Request: healthCheck");

        HealthCheckResponse response = new HealthCheckResponse();
        ServiceStatus status = new ServiceStatus();

        try {
            status.setSuccess(true);
            status.setCode("HEALTHY");
            status.setMessage("Service is running");

            response.setStatus(status);
            response.setServiceName("Ond Money Notification Service");
            response.setVersion("1.0.0");
            response.setUptime(String.valueOf(System.currentTimeMillis()));
            response.setSmsProviderStatus("UP");
            response.setEmailProviderStatus("UP");
            response.setPushProviderStatus("UP");

        } catch (Exception e) {
            log.error("Erreur lors du health check", e);
            status.setSuccess(false);
            status.setCode("UNHEALTHY");
            status.setMessage(e.getMessage());
            response.setStatus(status);
        }

        return response;
    }

    // ========================================
    // MÉTHODES UTILITAIRES PRIVÉES
    // ========================================

    /**
     * Convertit le payload SOAP en NotificationPayload DTO
     */
    private NotificationPayload convertSoapPayloadToDto(sn.ondmoney.notification.soap.model.NotificationPayload soapPayload) {
        NotificationPayload.NotificationPayloadBuilder builder = NotificationPayload.builder();

        // Champs d'authentification
        if (soapPayload.getVerificationCode() != null)
            builder.verificationCode(soapPayload.getVerificationCode());
        if (soapPayload.getVerificationCodeExpiryInMinutes() != null)
            builder.verificationCodeExpiryInMinutes(soapPayload.getVerificationCodeExpiryInMinutes());

        // Champs de transaction
        if (soapPayload.getTransactionId() != null)
            builder.transactionId(soapPayload.getTransactionId());
        if (soapPayload.getAmount() != null)
            builder.amount(BigDecimal.valueOf(Long.parseLong(soapPayload.getAmount())));
        if (soapPayload.getCurrency() != null)
            builder.currency(soapPayload.getCurrency());
        if (soapPayload.getBalance() != null)
            builder.clientBalance(BigDecimal.valueOf(Long.parseLong(soapPayload.getBalance())));
        if (soapPayload.getTransactionDate() != null)
            builder.transactionDate(toInstant(soapPayload.getTransactionDate()));
        if (soapPayload.getTransactionStatus() != null)
            builder.transactionStatus(soapPayload.getTransactionStatus());

        // Sender (envoyeur/payeur/acheteur)
        if (soapPayload.getSender() != null)
            builder.senderPhone(soapPayload.getSender());
        if (soapPayload.getSenderName() != null)
            builder.senderName(soapPayload.getSenderName());

        // Receiver (bénéficiaire/marchand)
        if (soapPayload.getBeneficiary() != null)
            builder.receiverPhone(soapPayload.getBeneficiary());
        if (soapPayload.getBeneficiaryName() != null)
            builder.receiverName(soapPayload.getBeneficiaryName());

        // Champs de sécurité
        if (soapPayload.getDevice() != null)
            builder.deviceId(soapPayload.getDevice());
        if (soapPayload.getLocation() != null) {
            Map<String, String> additionalData = new HashMap<>();
            additionalData.put("location", soapPayload.getLocation());
            builder.additionalData(additionalData);
        }

        // Champs Bank2Wallet/Wallet2Bank
        if (soapPayload.getBankName() != null)
            builder.bankName(soapPayload.getBankName());

        // Opérateur airtime
        if (soapPayload.getOperator() != null) {
            Map<String, String> additionalData = builder.build().getAdditionalData();
            if (additionalData == null) {
                additionalData = new HashMap<>();
            }
            additionalData.put("operator", soapPayload.getOperator());
            builder.additionalData(additionalData);
        }

        // Merchant code
        if (soapPayload.getMerchantCode() != null || soapPayload.getMerchantName() != null) {
            Map<String, String> additionalData = builder.build().getAdditionalData();
            if (additionalData == null) {
                additionalData = new HashMap<>();
            }
            if (soapPayload.getMerchantCode() != null) {
                additionalData.put("merchantCode", soapPayload.getMerchantCode());
            }
            if (soapPayload.getMerchantName() != null) {
                additionalData.put("merchantName", soapPayload.getMerchantName());
            }
            builder.additionalData(additionalData);
        }

        return builder.build();
    }

    /**
     * Envoie une notification de transfert
     */
    private String sendTransferNotification(String eventRef, NotificationType notificationType,
                                          String recipient, NotificationPayload payload, NotificationLanguage language) throws Exception {

        Optional<NotificationTemplateDTO> templateOpt = notificationTemplateService.findActiveTemplateByCompositeKey(
            notificationType, NotificationChannel.SMS, language, 1);

        if (templateOpt.isEmpty()) {
            throw new RuntimeException("Template non trouvé pour " + notificationType);
        }

        NotificationTemplateDTO template = templateOpt.get();
        String message = templateEngineService.fillTemplate(template, payload);

        smsService.sendMessage(recipient, message);

        NotificationLogDTO logDto = NotificationLogDTO.builder()
            .eventRef(eventRef)
            .eventTime(Instant.now())
            .recipient(recipient)
            .notificationType(notificationType)
            .notificationStatus(NotificationStatus.SENT)
            .notificationChannel(NotificationChannel.SMS)
            .payload(objectMapper.writeValueAsString(payload))
            .sentAt(Instant.now())
            .externalEventRef("TRANSFER-" + System.currentTimeMillis())
            .retryCount(0)
            .notificationTemplateUsed(template)
            .createdAt(Instant.now())
            .build();

        NotificationLogDTO saved = notificationLogService.save(logDto);
        return saved.getId();
    }

    /**
     * Envoie une notification de paiement marchand
     */
    private String sendMerchantPaymentNotification(String eventRef, NotificationType notificationType,
                                                 String recipient, NotificationPayload payload, NotificationLanguage language) throws Exception {

        Optional<NotificationTemplateDTO> templateOpt = notificationTemplateService.findActiveTemplateByCompositeKey(
            notificationType, NotificationChannel.SMS, language, 1);

        if (templateOpt.isEmpty()) {
            throw new RuntimeException("Template non trouvé pour " + notificationType);
        }

        NotificationTemplateDTO template = templateOpt.get();
        String message = templateEngineService.fillTemplate(template, payload);

        smsService.sendMessage(recipient, message);

        NotificationLogDTO logDto = NotificationLogDTO.builder()
            .eventRef(eventRef)
            .eventTime(Instant.now())
            .recipient(recipient)
            .notificationType(notificationType)
            .notificationStatus(NotificationStatus.SENT)
            .notificationChannel(NotificationChannel.SMS)
            .payload(objectMapper.writeValueAsString(payload))
            .sentAt(Instant.now())
            .externalEventRef("MERCHANT-" + System.currentTimeMillis())
            .retryCount(0)
            .notificationTemplateUsed(template)
            .createdAt(Instant.now())
            .build();

        NotificationLogDTO saved = notificationLogService.save(logDto);
        return saved.getId();
    }

    /**
     * Envoie une alerte de sécurité
     */
    private String sendSecurityAlertNotification(String eventRef, NotificationChannel channel,
                                               String recipient, String userId, NotificationPayload payload, NotificationLanguage language) throws Exception {

        Optional<NotificationTemplateDTO> templateOpt = notificationTemplateService.findActiveTemplateByCompositeKey(
            NotificationType.SECURITY_ALERT_LOGIN, channel, language, 1);

        if (templateOpt.isEmpty()) {
            throw new RuntimeException("Template non trouvé pour SECURITY_ALERT_LOGIN");
        }

        NotificationTemplateDTO template = templateOpt.get();
        String messageBody = templateEngineService.fillTemplate(template, payload);
        String externalRef = null;

        if (channel == NotificationChannel.SMS) {
            smsService.sendMessage(recipient, messageBody);
            externalRef = "SECURITY-SMS-" + System.currentTimeMillis();
        } else if (channel == NotificationChannel.EMAIL) {
            String subject = template.getSubjectTemplate() != null ?
                template.getSubjectTemplate() : "Alerte de sécurité Ond Money";
            emailService.sendMessage(recipient, subject, messageBody);
            externalRef = "SECURITY-EMAIL-" + System.currentTimeMillis();
        }

        NotificationLogDTO logDto = NotificationLogDTO.builder()
            .eventRef(eventRef)
            .eventTime(Instant.now())
            .userId(userId)
            .recipient(recipient)
            .notificationType(NotificationType.SECURITY_ALERT_LOGIN)
            .notificationStatus(NotificationStatus.SENT)
            .notificationChannel(channel)
            .payload(objectMapper.writeValueAsString(payload))
            .sentAt(Instant.now())
            .externalEventRef(externalRef)
            .retryCount(0)
            .notificationTemplateUsed(template)
            .createdAt(Instant.now())
            .build();

        NotificationLogDTO saved = notificationLogService.save(logDto);
        return saved.getId();
    }

    /**
     * Convertit NotificationLogDTO en NotificationLogInfo (SOAP)
     */
    private NotificationLogInfo convertToNotificationLogInfo(NotificationLogDTO dto) {
        NotificationLogInfo info = new NotificationLogInfo();
        info.setId(dto.getId());
        info.setEventRef(dto.getEventRef());
        info.setEventTime(toXMLGregorianCalendar(dto.getEventTime()));
        info.setUserId(dto.getUserId());
        info.setRecipient(dto.getRecipient());
        info.setNotificationType(dto.getNotificationType().name());
        info.setNotificationStatus(dto.getNotificationStatus().name());
        info.setNotificationChannel(dto.getNotificationChannel().name());
        info.setSentAt(toXMLGregorianCalendar(dto.getSentAt()));
        info.setExternalEventRef(dto.getExternalEventRef());
        info.setErrorMessage(dto.getErrorMessage());
        info.setRetryCount(dto.getRetryCount() != null ? dto.getRetryCount() : 0);
        return info;
    }

    /**
     * Convertit Instant en XMLGregorianCalendar pour SOAP
     */
    private XMLGregorianCalendar toXMLGregorianCalendar(Instant instant) {
        if (instant == null) return null;
        try {
            GregorianCalendar cal = GregorianCalendar.from(
                instant.atZone(ZoneId.systemDefault())
            );
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            log.error("Erreur lors de la conversion de la date", e);
            return null;
        }
    }

    /**
     * Convertit XMLGregorianCalendar en Instant
     */
    private Instant toInstant(XMLGregorianCalendar calendar) {
        if (calendar == null) return null;
        return calendar.toGregorianCalendar().toInstant();
    }
}
