package net.chess_platform.chat_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.chat_service.dto.RelationshipSearchDto;
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

    @PostMapping("/search")
    public RelationshipDto search(@RequestBody RelationshipSearchDto request, CurrentUser currentUser) {
        return relationShipService.getRelationship(request.ids(), currentUser);
    }
}
