package com.softdesign.tourney.publisher;

import com.softdesign.tourney.config.RabbitMQConfig;
import com.softdesign.tourney.event.TournamentEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TournamentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(TournamentEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public TournamentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCreated(String tournamentName, String username) {
        publish(RabbitMQConfig.KEY_CREATED, "CREATED", tournamentName, username);
    }

    public void publishUpdated(String tournamentName, String username) {
        publish(RabbitMQConfig.KEY_UPDATED, "UPDATED", tournamentName, username);
    }

    public void publishDeleted(String tournamentName, String username) {
        publish(RabbitMQConfig.KEY_DELETED, "DELETED", tournamentName, username);
    }

    private void publish(String routingKey, String action, String tournamentName, String username) {
        TournamentEventMessage msg = new TournamentEventMessage(action, tournamentName, username);
        log.info("Publishing tournament event: routingKey={}, tournament={}", routingKey, tournamentName);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, routingKey, msg);
    }
}