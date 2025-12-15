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
public class SmsNotificationRequest implements Serializable {

    private String receiverPhone;
    private String text;
}
