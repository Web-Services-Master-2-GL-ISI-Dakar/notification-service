package sn.ondmoney.notificationservice.service.handler;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import sn.ondmoney.notificationservice.service.dto.NotificationDTO;

/**
 * Handler pour l'envoi de notifications Push via Firebase Cloud Messaging (FCM)
 */
@Component
public class PushHandler {

    private final Logger log = LoggerFactory.getLogger(PushHandler.class);

    @Value("${application.notification.push.firebase.credentials-path}")
    private String credentialsPath;

    @Value("${application.notification.push.enabled:true}")
    private boolean enabled;

    private boolean firebaseInitialized = false;

    /**
     * Initialise Firebase au démarrage de l'application
     */
    @PostConstruct
    public void initialize() {
        if (!enabled) {
            log.info("Push notifications are disabled");
            return;
        }

        try {
            // Charger les credentials Firebase
            InputStream serviceAccount = new ClassPathResource(credentialsPath).getInputStream();

            FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

            // Initialiser Firebase si pas déjà fait
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                firebaseInitialized = true;
                log.info("Firebase initialized successfully for push notifications");
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase", e);
            firebaseInitialized = false;
        }
    }

    /**
     * Envoie une notification Push
     *
     * @param notification La notification contenant le message et le device token
     * @return true si l'envoi a réussi, false sinon
     */
    public boolean send(NotificationDTO notification) {
        if (!enabled) {
            log.warn("Push notifications are disabled in configuration");
            return false;
        }

        if (!firebaseInitialized) {
            log.error("Firebase is not initialized");
            return false;
        }

        log.debug("Sending push notification to device: {}", notification.getRecipient());

        try {
            // Valider le device token
            String deviceToken = notification.getRecipient();
            if (deviceToken == null || deviceToken.isEmpty()) {
                log.error("Invalid device token");
                return false;
            }

            // Construire le message FCM
            Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(Notification.builder().setTitle(notification.getTitle()).setBody(notification.getMessage()).build())
                .putData("notificationId", notification.getId().toString())
                .putData("type", notification.getType().toString())
                .build();

            // Envoyer via Firebase
            String response = FirebaseMessaging.getInstance().send(message);

            log.info("Push notification sent successfully. Message ID: {}", response);
            return true;
        } catch (Exception e) {
            log.error("Error sending push notification", e);
            return false;
        }
    }
}
