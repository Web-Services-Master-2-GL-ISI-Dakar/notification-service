package sn.ondmoney.notificationservice.service.handler;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import sn.ondmoney.notificationservice.service.dto.NotificationDTO;

/**
 * Handler pour l'envoi d'emails via SMTP
 */
@Component
public class EmailHandler {

    private final Logger log = LoggerFactory.getLogger(EmailHandler.class);

    @Value("${application.notification.email.from-address}")
    private String fromAddress;

    @Value("${application.notification.email.from-name}")
    private String fromName;

    @Value("${application.notification.email.enabled:true}")
    private boolean enabled;

    private final JavaMailSender mailSender;

    public EmailHandler(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envoie un email
     *
     * @param notification La notification contenant le message et le destinataire
     * @return true si l'envoi a réussi, false sinon
     */
    public boolean send(NotificationDTO notification) {
        if (!enabled) {
            log.warn("Email sending is disabled in configuration");
            return false;
        }

        log.debug("Sending email to: {}", notification.getRecipient());

        try {
            // Valider l'adresse email
            if (!isValidEmail(notification.getRecipient())) {
                log.error("Invalid email address: {}", notification.getRecipient());
                return false;
            }

            // Créer le message
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromAddress, fromName);
            helper.setTo(notification.getRecipient());
            helper.setSubject(notification.getTitle());
            helper.setText(buildHtmlContent(notification), true);

            // Envoyer
            mailSender.send(mimeMessage);

            log.info("Email sent successfully to: {}", notification.getRecipient());
            return true;
        } catch (MailException | MessagingException e) {
            log.error("Error sending email to: {}", notification.getRecipient(), e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error sending email", e);
            return false;
        }
    }

    /**
     * Construit le contenu HTML de l'email
     */
    private String buildHtmlContent(NotificationDTO notification) {
        return (
            "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "<meta charset='UTF-8'>" +
            "<style>" +
            "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
            ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
            ".header { background-color: #007bff; color: white; padding: 20px; text-align: center; }" +
            ".content { background-color: #f8f9fa; padding: 20px; margin: 20px 0; }" +
            ".footer { text-align: center; color: #666; font-size: 12px; padding: 20px; }" +
            "</style>" +
            "</head>" +
            "<body>" +
            "<div class='container'>" +
            "<div class='header'>" +
            "<h1>OndMoney</h1>" +
            "</div>" +
            "<div class='content'>" +
            "<h2>" +
            notification.getTitle() +
            "</h2>" +
            "<p>" +
            notification.getMessage().replace("\n", "<br>") +
            "</p>" +
            "</div>" +
            "<div class='footer'>" +
            "<p>Cet email a été envoyé par OndMoney. Ne pas répondre à cet email.</p>" +
            "<p>&copy; 2025 OndMoney. Tous droits réservés.</p>" +
            "</div>" +
            "</div>" +
            "</body>" +
            "</html>"
        );
    }

    /**
     * Valide une adresse email
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}
