package net.chess_platform.chat_service.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.chat_service.dto.RelationshipDto;
import net.chess_platform.chat_service.service.RelationshipService;
import net.chess_platform.common.security.CurrentUser;

@RestController
@RequestMapping("/api/relationships")
public class RelationshipController {

    private RelationshipService relationShipService;

    public RelationshipController(RelationshipService relationShipService) {
        this.relationShipService = relationShipService;
    }

    @GetMapping
    public RelationshipDto search(@RequestParam UUID userId, @RequestParam UUID userId1, @RequestParam UUID userId2,
            CurrentUser currentUser) {
        if (userId != null) {
            return relationShipService.queryRelationship(userId, currentUser.id(), currentUser);
        }
        
        if (userId1 == null || userId2 == null) {
            throw new IllegalArgumentException();
        }

        return relationShipService.queryRelationship(userId1, userId2, currentUser); 
        
    }
}
