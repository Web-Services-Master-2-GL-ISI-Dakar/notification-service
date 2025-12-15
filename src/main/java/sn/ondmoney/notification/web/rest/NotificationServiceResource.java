package sn.ondmoney.notification.web.rest;

import jakarta.validation.Valid;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.ondmoney.notification.broker.NotificationEventProducer;
import sn.ondmoney.notification.domain.enumeration.NotificationChannel;
import sn.ondmoney.notification.domain.enumeration.NotificationLanguage;
import sn.ondmoney.notification.domain.enumeration.NotificationStatus;
import sn.ondmoney.notification.service.*;
import sn.ondmoney.notification.service.dto.*;
import sn.ondmoney.notification.service.mapper.NotificationLogMapper;
import tech.jhipster.web.util.HeaderUtil;

@RestController
@RequestMapping("/api/notification-service")
public class NotificationServiceResource {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceResource.class);
    private static final String ENTITY_NAME = "ondmoneyNotificationService";
    private final NotificationEventProducer notificationEventProducer;
    private final NotificationLogService notificationLogService;
    private final NotificationTemplateService notificationTemplateService;
    private final TemplateEngineService templateEngineService;
    private final NotificationLogMapper notificationLogMapper;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    // Notification Services
    private final EmailService emailService;
    private final SmsService smsService;
    private final FCMService fCMService;

    public NotificationServiceResource(
        EmailService emailService,
        FCMService fCMService,
        @Qualifier("lafricaMobileService") SmsService smsService,
        NotificationEventProducer notificationEventProducer,
        NotificationLogService notificationLogService,
        NotificationTemplateService notificationTemplateService,
        TemplateEngineService templateEngineService,
        NotificationLogMapper notificationLogMapper
    ) {
        this.emailService = emailService;
        this.fCMService = fCMService;
        this.smsService = smsService;
        this.notificationEventProducer = notificationEventProducer;
        this.notificationLogService = notificationLogService;
        this.notificationTemplateService = notificationTemplateService;
        this.templateEngineService = templateEngineService;
        this.notificationLogMapper = notificationLogMapper;
    }

    @PostMapping("/send-email")
    public ResponseEntity<Void> sendEmailNotification(@Valid @RequestBody SendEmailRequest request) throws URISyntaxException {
        LOG.debug("REST request to send SendEmailRequest : {}", request);
        emailService.sendMessage(request.getToEmail(), request.getSubject(), request.getContent());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, request.getToEmail()))
            .build();
    }

    @PostMapping("/send/email")
    public ResponseEntity sendEmail(@Valid @RequestBody NotificationRequest request) throws URISyntaxException {
        LOG.debug("REST request to send notificationRequest : {} through EMAIL", request);
        NotificationChannel notificationChannel = NotificationChannel.EMAIL;
        NotificationTemplateDTO template = notificationTemplateService
            .findActiveTemplateByCompositeKey(request.getNotificationType(), notificationChannel, NotificationLanguage.FR, 1)
            .orElseThrow(() -> new RuntimeException("No template found for the given parameters"));
        // Replace the information from the request in the slots within the notification payload
        String finalMessage = templateEngineService.fillTemplate(template, request.getPayload());
        emailService.sendMessage(request.getPayload().getSenderPhone(), template.getSubjectTemplate(), finalMessage);

        // Log the notification in MongoDB
        NotificationLogDTO notificationLog = notificationLogMapper.fromNotificationRequest(request);
        notificationLog.setNotificationStatus(NotificationStatus.SENT);
        notificationLog.setRecipient(request.getPayload().getSenderPhone());
        notificationLog.setNotificationChannel(notificationChannel);
        notificationLog.setNotificationType(request.getNotificationType());
        notificationLog.setNotificationTemplateUsed(template);
        notificationLog.setSentAt(Instant.now());
        notificationLogService.save(notificationLog);

        DeliveryReceipt receipt = notificationLogMapper.toDeliveryReceipt(notificationLog);
        // Build the notification response from the receipt
        NotificationResponse notificationResponse = NotificationResponse.builder()
            .eventRef(receipt.getEventRef())
            .eventTime(receipt.getEventTime())
            .notificationStatus(NotificationStatus.SENT)
            .notificationType(request.getNotificationType())
            .deliveryReceipts(List.of(receipt))
            .payload(request.getPayload().toString())
            .build();

        notificationEventProducer.publish(notificationLog);
        return new ResponseEntity<>(notificationResponse, HttpStatus.OK);
    }

    @PostMapping("/send-sms")
    public ResponseEntity sendSmsNotification(@RequestBody SmsNotificationRequest request) throws ExecutionException, InterruptedException {
        LOG.debug("REST request to send SmsNotificationRequest : {}", request);
        smsService.sendMessage(request.getReceiverPhone(), request.getText());
        return new ResponseEntity<>(new SmsNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }

    @PostMapping("/send/sms")
    public ResponseEntity sendSms(@RequestBody NotificationRequest request) throws ExecutionException, InterruptedException {
        LOG.debug("REST request to send SmsNotificationRequest : {}", request);
        NotificationChannel notificationChannel = NotificationChannel.SMS;
        NotificationTemplateDTO template = notificationTemplateService
            .findActiveTemplateByCompositeKey(request.getNotificationType(), notificationChannel, NotificationLanguage.FR, 1)
            .orElseThrow(() -> new RuntimeException("No template found for the given parameters"));
        // Replace the information from the request in the slots within the notification payload
        String finalMessage = templateEngineService.fillTemplate(template, request.getPayload());

        LOG.info("Sending message: {}", finalMessage);
        smsService.sendMessage(request.getPayload().getSenderPhone(), finalMessage);

        // Log the notification in MongoDB
        NotificationLogDTO notificationLog = notificationLogMapper.fromNotificationRequest(request);
        notificationLog.setNotificationStatus(NotificationStatus.SENT);
        notificationLog.setRecipient(request.getPayload().getSenderPhone());
        notificationLog.setNotificationChannel(notificationChannel);
        notificationLog.setNotificationType(request.getNotificationType());
        notificationLog.setNotificationTemplateUsed(template);
        notificationLog.setSentAt(Instant.now());
        notificationLogService.save(notificationLog);

        DeliveryReceipt receipt = notificationLogMapper.toDeliveryReceipt(notificationLog);
        // Build the notification response from the receipt
        NotificationResponse notificationResponse = NotificationResponse.builder()
            .eventRef(receipt.getEventRef())
            .eventTime(receipt.getEventTime())
            .notificationStatus(NotificationStatus.SENT)
            .notificationType(request.getNotificationType())
            .deliveryReceipts(List.of(receipt))
            .payload(request.getPayload().toString())
            .build();

        notificationEventProducer.publish(notificationLog);
        return new ResponseEntity<>(notificationResponse, HttpStatus.OK);
    }

    @PostMapping("/send-push")
    public ResponseEntity sendPushNotification(@RequestBody PushNotificationRequest request)
        throws ExecutionException, InterruptedException {
        fCMService.sendMessageToToken(request);
        return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Notification has been sent."), HttpStatus.OK);
    }
}
