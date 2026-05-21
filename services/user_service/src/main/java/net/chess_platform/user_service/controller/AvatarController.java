package net.chess_platform.user_service.controller;

import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.user_service.dto.AvatarDto;
import net.chess_platform.user_service.service.AvatarService;

@RestController
@RequestMapping("/api/avatars")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping("/{id}")
    public Resource getAvatar(@PathVariable UUID id) {
        return avatarService.download(id);
    }

    @PostMapping
    public AvatarDto uploadAvatar(MultipartFile file, CurrentUser currentUser) {
        return avatarService.upload(file, currentUser);
    }

    @DeleteMapping("/me")
    public AvatarDto deleteAvatar(CurrentUser currentUser) {
        return avatarService.delete(currentUser);
    }
}
