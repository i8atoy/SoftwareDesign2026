package com.softdesign.tourney.service;

import com.softdesign.tourney.event.ResourceEvent;
import com.softdesign.tourney.models.UserEntity;
import com.softdesign.tourney.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Autowired(required = false)
    public NotificationService(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    @EventListener
    public void handleResourceEvent(ResourceEvent event) {
        UserEntity user = userRepository.findUserByUserName(event.getTriggeredByUsername());

        if (user != null) {
            String subject = "Tournament System Notification: " + event.getAction();
            String text = String.format("Hello %s,\n\nThe %s ('%s') was successfully %s by %s.",
                    user.getUserName(), event.getResourceType(), event.getResourceIdentifier(),
                    event.getAction(), event.getTriggeredByUsername());

            try {
                if (mailSender != null) {
                    sendEmail("joshuapop1782004@gmail.com", subject, text);
                } else {
                    System.out.println("MailSender not configured." + text);
                }
            } catch (MessagingException e) {
                System.err.println("Failed to send email: " + e.getMessage());
            }
        }
    }

    public void sendEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        helper.setFrom("joshuapop1782004@gmail.com");

        mailSender.send(mimeMessage);

        System.out.println("Notification email sent successfully to: " + to);
    }
}