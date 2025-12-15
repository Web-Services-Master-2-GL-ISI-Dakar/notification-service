package sn.ondmoney.notification.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import sn.ondmoney.notification.service.dto.NotificationLogDTO;

@Component("notificationHistoryEventProducer")
public class NotificationHistoryEventProducer implements Supplier<Message<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationHistoryEventProducer.class);

    private final BlockingQueue<NotificationLogDTO> queue = new LinkedBlockingQueue<>();
    private final ObjectMapper objectMapper;

    public NotificationHistoryEventProducer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Message<String> get() {
        NotificationLogDTO event = queue.poll();
        if (event == null) return null;

        try {
            String json = objectMapper.writeValueAsString(event);
            LOG.info("Publishing history event: type={}, transactionId={}", event.getNotificationType(), event.getEventRef());
            return MessageBuilder.withPayload(json).setHeader("contentType", "application/json").build();
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize NotificationLogDTO", e);
            return null;
        }
    }

    public void publish(NotificationLogDTO event) {
        LOG.debug("Queueing history event: {}", event.getEventRef());
        queue.offer(event);
    }
}
