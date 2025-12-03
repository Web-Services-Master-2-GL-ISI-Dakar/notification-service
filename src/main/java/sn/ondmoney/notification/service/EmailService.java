package sn.ondmoney.notification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Envoi d'un mail de vérification lors de l'inscription (Example method)
     * NOTE: This is just a placeholder. When using HTML via the API,
     * the HTML is usually passed directly in the 'content' field.
     */
    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "Vérification de votre compte utilisateur";
        String verificationUrl = "http://localhost:8081/api/v0/auth/verify?token=" + token;

        // Simple plain text version
        String textContent =
            "Bonjour,\n\n" +
            "Merci de vous être inscrit sur la plateforme Ond Money.\n" +
            "Veuillez cliquer sur le lien ci-dessous pour activer votre compte :\n\n" +
            verificationUrl +
            "\n\n" +
            "Cordialement,\nL’équipe Ond Money";

        // To use the full HTML template here, you would load the template,
        // replace the placeholder, and call sendMimeMessage with the HTML content.
        // For this example, we'll send it as plain text using the generic method.
        // In a real app, you would define a specific method for each HTML template.
        sendMessage(toEmail, subject, textContent);
    }

    /**
     * Méthode générique pour envoyer un email (Plain Text or HTML)
     * This method decides whether to treat the input content as HTML or plain text.
     */
    public void sendMessage(String toEmail, String subject, String content) {
        // Simple check: If the content looks like it starts with HTML, treat it as such.
        // In a production system, you might add a boolean flag to the API request instead.
        boolean isHtml = content != null && content.trim().toLowerCase().startsWith("<!doctype html>");

        try {
            if (isHtml) {
                // Send as MimeMessage with both text (fallback) and HTML
                // For a generic API, we use a simple text fallback.
                String textFallback = "Email content not available in plain text. Please view in a compatible client.";
                sendMimeMessage(toEmail, subject, textFallback, content);
            } else {
                // Send as MimeMessage with only plain text
                sendMimeMessage(toEmail, subject, content, null);
            }
        } catch (MessagingException e) {
            log.error("Failed to send email: " + e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }

    /**
     * Core method to send an email with optional HTML support.
     * @param htmlContent The HTML body (can be null for plain text only).
     * @param textContent The plain text body (mandatory for accessibility/fallback).
     */
    public void sendMimeMessage(String toEmail, String subject, String textContent, String htmlContent) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        // The 'true' flag here indicates 'multipart/mixed' for attachments,
        // or 'multipart/alternative' if only HTML and text are provided.
        // The "UTF-8" ensures character encoding support.
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject(subject);

        if (htmlContent != null) {
            // setText(String text, boolean html) is used when only the content is available.
            // When we have both text and html, we use setText(String text, String html)
            helper.setText(textContent, htmlContent);
        } else {
            // Fallback to plain text only
            helper.setText(textContent);
        }

        mailSender.send(mimeMessage);
    }
}
