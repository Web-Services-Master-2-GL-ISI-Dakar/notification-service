package sn.ondmoney.notification.service.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendEmailRequest implements Serializable {

    private String toEmail;
    private String subject;
    private String content;
}
