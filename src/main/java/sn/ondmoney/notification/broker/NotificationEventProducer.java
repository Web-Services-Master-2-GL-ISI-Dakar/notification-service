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

@Component("notificationEventProducer")
public class NotificationEventProducer implements Supplier<Message<String>> {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationEventProducer.class);

    private final BlockingQueue<NotificationLogDTO> queue = new LinkedBlockingQueue<NotificationLogDTO>();
    private final ObjectMapper objectMapper;

    public NotificationEventProducer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Message<String> get() {
        NotificationLogDTO tx = queue.poll();
        if (tx == null) return null;

        try {
            String json = objectMapper.writeValueAsString(tx);
            LOG.info("Publishing raw notification: {}", tx.getEventRef());
            return MessageBuilder.withPayload(json).setHeader("contentType", "application/json").build();
        } catch (JsonProcessingException e) {
            LOG.error("Failed to serialize NotificationLog", e);
            return null;
        }
    }

    public void publish(NotificationLogDTO tx) {
        LOG.debug("Queueing raw notification: {}", tx.getEventRef());
        queue.offer(tx);
    }
}
