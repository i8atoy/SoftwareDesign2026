package com.softdesign.tourney.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TournamentCommandExecutor {

    private static final Logger log = LoggerFactory.getLogger(TournamentCommandExecutor.class);

    public void execute(TournamentCommand command) {
        log.info("Executing command: {}", command.getClass().getSimpleName());
        command.execute();
        log.info("Command completed: {}", command.getClass().getSimpleName());
    }
}