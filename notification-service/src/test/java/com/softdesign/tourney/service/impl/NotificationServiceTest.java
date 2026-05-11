package com.softdesign.tourney.service.impl;

import com.softdesign.tourney.event.TournamentEventMessage;
import com.softdesign.tourney.service.NotificationService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationService, "toAddress", "test@example.com");
        ReflectionTestUtils.setField(notificationService, "fromAddress", "noreply@example.com");
    }

    @Test
    void handleTournamentEvent_sendsEmailOnCreated() throws Exception {
        TournamentEventMessage event = new TournamentEventMessage("CREATED", "IEM Cologne", "admin");

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        notificationService.handleTournamentEvent(event);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void handleTournamentEvent_sendsEmailOnUpdated() throws Exception {
        TournamentEventMessage event = new TournamentEventMessage("UPDATED", "IEM Cologne", "admin");

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        notificationService.handleTournamentEvent(event);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void handleTournamentEvent_sendsEmailOnDeleted() throws Exception {
        TournamentEventMessage event = new TournamentEventMessage("DELETED", "IEM Cologne", "admin");

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        notificationService.handleTournamentEvent(event);

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void handleTournamentEvent_doesNotThrowOnMailFailure() {
        TournamentEventMessage event = new TournamentEventMessage("CREATED", "IEM Cologne", "admin");

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(MimeMessage.class));

        // Should log the error but not propagate the exception
        assertDoesNotThrow(() -> notificationService.handleTournamentEvent(event));
    }
}