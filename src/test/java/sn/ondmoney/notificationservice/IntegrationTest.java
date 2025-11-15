package sn.ondmoney.notificationservice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import sn.ondmoney.notificationservice.config.AsyncSyncConfiguration;
import sn.ondmoney.notificationservice.config.EmbeddedKafka;
import sn.ondmoney.notificationservice.config.EmbeddedSQL;
import sn.ondmoney.notificationservice.config.JacksonConfiguration;
import sn.ondmoney.notificationservice.config.TestSecurityConfiguration;

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
@EmbeddedSQL
@EmbeddedKafka
public @interface IntegrationTest {
}
