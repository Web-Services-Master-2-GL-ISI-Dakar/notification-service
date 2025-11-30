package sn.ondmoney.notification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import sn.ondmoney.notification.config.AsyncSyncConfiguration;
import sn.ondmoney.notification.config.EmbeddedElasticsearch;
import sn.ondmoney.notification.config.EmbeddedKafka;
import sn.ondmoney.notification.config.EmbeddedMongo;
import sn.ondmoney.notification.config.EmbeddedRedis;
import sn.ondmoney.notification.config.JacksonConfiguration;
import sn.ondmoney.notification.config.TestSecurityConfiguration;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        OndmoneyNotificationServiceApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class,
    }
)
@EmbeddedRedis
@EmbeddedMongo
@EmbeddedElasticsearch
@EmbeddedKafka
public @interface IntegrationTest {
}
