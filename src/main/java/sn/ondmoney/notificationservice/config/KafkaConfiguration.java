package sn.ondmoney.notificationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configuration des topics Kafka
 * Ces topics permettent la communication asynchrone entre microservices
 */
@Configuration
public class KafkaConfiguration {

    /**
     * Topic pour recevoir les événements de transaction
     * Le microservice Transaction va publier ici
     */
    @Bean
    public NewTopic transactionTopic() {
        return TopicBuilder.name("transactions").partitions(3).replicas(1).build();
    }

    /**
     * Topic pour les événements de compte
     */
    @Bean
    public NewTopic accountTopic() {
        return TopicBuilder.name("accounts").partitions(3).replicas(1).build();
    }

    /**
     * Topic pour les alertes de sécurité
     */
    @Bean
    public NewTopic securityEventsTopic() {
        return TopicBuilder.name("security-events").partitions(3).replicas(1).build();
    }

    /**
     * Topic pour publier les notifications envoyées
     * (pour audit ou autres microservices)
     */
    @Bean
    public NewTopic notificationsTopic() {
        return TopicBuilder.name("notifications").partitions(3).replicas(1).build();
    }

    /**
     * Topic Dead Letter Queue pour les messages en échec
     */
    @Bean
    public NewTopic deadLetterTopic() {
        return TopicBuilder.name("notifications-dlq").partitions(1).replicas(1).build();
    }
}
