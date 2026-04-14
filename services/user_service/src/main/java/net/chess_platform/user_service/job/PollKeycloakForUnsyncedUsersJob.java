package net.chess_platform.user_service.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.chess_platform.user_service.service.UserService;

@Component
public class PollKeycloakForUnsyncedUsersJob {

    private UserService userService;

    public PollKeycloakForUnsyncedUsersJob(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 10000)
    public void run() {
        userService.syncUnsyncedUsers();
    }
}
