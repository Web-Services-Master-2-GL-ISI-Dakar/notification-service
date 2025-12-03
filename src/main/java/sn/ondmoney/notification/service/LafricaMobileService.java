package sn.ondmoney.notification.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class LafricaMobileService implements SmsService {

    private final RestTemplate restTemplate;

    private final String baseUrl;
    private final String accountId;
    private final String password;
    private final String sender;

    public LafricaMobileService(
        RestTemplateBuilder restTemplateBuilder,
        @Value("${lafrica-mobile.base-url}") String baseUrl,
        @Value("${lafrica-mobile.accountid}") String accountId,
        @Value("${lafrica-mobile.password}") String password,
        @Value("${lafrica-mobile.sender}") String sender
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.baseUrl = baseUrl;
        this.accountId = accountId;
        this.password = password;
        this.sender = sender;
    }

    @Override
    public void sendMessage(String to, String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("accountid", accountId);
        body.put("password", password);
        body.put("sender", sender);
        body.put("text", text);
        body.put("to", to);
        // Generates a unique ID for this request (ret_id)
        body.put("ret_id", UUID.randomUUID().toString());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String notificationId = response.getBody();
                log.info("SMS sent successfully to {}. Provider ID: {}", to, notificationId);
            }
        } catch (HttpClientErrorException e) {
            // Handle 400 (Bad Request) and 401 (Unauthorized)
            String errorPhrase = e.getResponseBodyAsString();
            log.error("SMS Provider Error ({}): {}", e.getStatusCode(), errorPhrase);
            throw new RuntimeException("Échec de l'envoi du message par SMS");
        } catch (HttpServerErrorException e) {
            // Handle 500+ errors
            log.error("SMS Provider Server Error ({}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Échec de l'envoi du message par SMS");
        } catch (Exception e) {
            log.error("Unexpected error while sending SMS", e);
            throw new RuntimeException("Échec de l'envoi du message par SMS");
        }
    }
}
