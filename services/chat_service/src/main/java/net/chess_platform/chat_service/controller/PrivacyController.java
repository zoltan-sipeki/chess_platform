package net.chess_platform.chat_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.chat_service.dto.PrivacyDto;
import net.chess_platform.chat_service.service.PrivacyService;
import net.chess_platform.common.security.CurrentUser;

@RestController
@RequestMapping("/api/privacy")
public class PrivacyController {

    private final PrivacyService privacyService;

    public PrivacyController(PrivacyService privacyService) {
        this.privacyService = privacyService;
    }

    @GetMapping
    public PrivacyDto getPrivacy(CurrentUser currentUser) {
        return privacyService.findAll(currentUser);
    }

    @PatchMapping
    public void updatePrivacy(@RequestBody PrivacyDto dto, CurrentUser currentUser) {
        privacyService.update(dto, currentUser);
    }

}
