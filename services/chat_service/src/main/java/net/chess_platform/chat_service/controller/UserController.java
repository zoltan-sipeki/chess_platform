package net.chess_platform.chat_service.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.chat_service.dto.ContactsDto;
import net.chess_platform.chat_service.service.RelationshipService;
import net.chess_platform.common.security.CurrentUser;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private RelationshipService relationshipService;

    public UserController(RelationshipService relationShipService) {
        this.relationshipService = relationShipService;
    }

    @GetMapping("/{userId}/contacts")
    public ContactsDto getContacts(@PathVariable UUID userId, CurrentUser currentUser) {
        return relationshipService.findContacts(userId, currentUser);
    }

}
