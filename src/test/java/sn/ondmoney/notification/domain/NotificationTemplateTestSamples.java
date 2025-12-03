package sn.ondmoney.notification.domain;

import java.util.UUID;

public class NotificationTemplateTestSamples {

    public static NotificationTemplate getNotificationTemplateSample1() {
        return new NotificationTemplate().id("id1").templateCode("templateCode1").subjectTemplate("subjectTemplate1");
    }

    public static NotificationTemplate getNotificationTemplateSample2() {
        return new NotificationTemplate().id("id2").templateCode("templateCode2").subjectTemplate("subjectTemplate2");
    }

    public static NotificationTemplate getNotificationTemplateRandomSampleGenerator() {
        return new NotificationTemplate()
            .id(UUID.randomUUID().toString())
            .templateCode(UUID.randomUUID().toString())
            .subjectTemplate(UUID.randomUUID().toString());
    }
}
