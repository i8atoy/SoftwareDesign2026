package com.softdesign.tourney.service;

import com.softdesign.tourney.config.RabbitMQConfig;
import com.softdesign.tourney.event.TournamentEventMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;

    @Value("${notification.email.to}")
    private String toAddress;

    @Value("${notification.email.from}")
    private String fromAddress;

    @Autowired
    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void handleTournamentEvent(TournamentEventMessage event) {
        log.info("Received tournament event: action={}, tournament={}, user={}",
                event.getAction(), event.getTournamentName(), event.getTriggeredByUsername());

        String subject = buildSubject(event);
        String body    = buildBody(event);

        try {
            sendEmail(toAddress, subject, body);
            log.info("Notification email sent for action={}", event.getAction());
        } catch (MessagingException e) {
            log.error("Failed to send notification email: {}", e.getMessage());
        }
    }

    private String buildSubject(TournamentEventMessage event) {
        return String.format("Tournament %s: %s", event.getAction(), event.getTournamentName());
    }

    private String buildBody(TournamentEventMessage event) {
        return String.format(
                "Hello,\n\nThe tournament '%s' was successfully %s by user '%s'.\n\nTournament Pro",
                event.getTournamentName(),
                event.getAction().toLowerCase(),
                event.getTriggeredByUsername()
        );
    }

    public void sendEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false);
        helper.setTo(to);
        helper.setFrom(fromAddress);
        helper.setSubject(subject);
        helper.setText(text);
        mailSender.send(message);
    }
}