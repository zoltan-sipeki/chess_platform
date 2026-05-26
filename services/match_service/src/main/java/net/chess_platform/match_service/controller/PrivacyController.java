package net.chess_platform.match_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.match_service.dto.PrivacyDto;
import net.chess_platform.match_service.service.PrivacyService;

@RestController
@RequestMapping("/api/privacy")
public class PrivacyController {

    private final PrivacyService privacyService;

    public PrivacyController(PrivacyService privacyService) {
        this.privacyService = privacyService;
    }

    @GetMapping
    public PrivacyDto getPrivacy(CurrentUser currentUser) {
        return privacyService.getPrivacySettings(currentUser);
    }

    @PatchMapping
    public void updatePrivacy(@RequestBody PrivacyDto request, CurrentUser currentUser) {
        privacyService.updatePrivacySettings(request, currentUser);
    }
}
