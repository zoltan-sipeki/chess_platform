package net.chess_platform.relay_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.relay_service.dto.PreferredPresenceUpdateDto;
import net.chess_platform.relay_service.service.RelayUserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final RelayUserService relayUserService;

    public UserController(RelayUserService relayUserService) {
        this.relayUserService = relayUserService;
    }

    @PutMapping("/me/preferred-presence")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePreferredPresence(@RequestBody PreferredPresenceUpdateDto dto , CurrentUser currentUser) {
        relayUserService.updatePreferredPresence(dto.presence(), currentUser);
    }

}
