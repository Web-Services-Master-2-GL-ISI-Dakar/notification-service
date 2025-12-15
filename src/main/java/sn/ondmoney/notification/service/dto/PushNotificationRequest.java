package sn.ondmoney.notification.service.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PushNotificationRequest implements Serializable {

    private String title;
    private String body;
    private String topic;
    private String token;
}
