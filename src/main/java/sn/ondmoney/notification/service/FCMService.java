package sn.ondmoney.notification.service;

import com.google.firebase.messaging.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sn.ondmoney.notification.service.dto.PushNotificationRequest;

// https://imgur.com/a/dHUquho the Logo of the Notification

@Service
public class FCMService {

    private final Logger logger = LoggerFactory.getLogger(FCMService.class);

    public void sendMessageToToken(PushNotificationRequest request) throws InterruptedException, ExecutionException {
        Message message = getPreconfiguredMessageToToken(request);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(message);
        String response = sendAndGetResponse(message);
        logger.info("Sent message to token. Device token: " + request.getToken() + ", " + response + " msg " + jsonOutput);
    }

    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    private AndroidConfig getAndroidConfig(String topic) {
        return AndroidConfig.builder()
            .setTtl(Duration.ofMinutes(2).toMillis())
            .setCollapseKey(topic)
            .setPriority(AndroidConfig.Priority.HIGH)
            .setNotification(AndroidNotification.builder().setTag(topic).build())
            .build();
    }

    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder().setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build();
    }

    private Message getPreconfiguredMessageToToken(PushNotificationRequest request) {
        return getPreconfiguredMessageBuilder(request).setToken(request.getToken()).build();
    }

    private Message.Builder getPreconfiguredMessageBuilder(PushNotificationRequest request) {
        AndroidConfig androidConfig = getAndroidConfig(request.getTopic());
        ApnsConfig apnsConfig = getApnsConfig(request.getTopic());
        Notification notification = Notification.builder().setTitle(request.getTitle()).setBody(request.getBody()).build();
        return Message.builder().setApnsConfig(apnsConfig).setAndroidConfig(androidConfig).setNotification(notification);
    }
}
