package net.chess_platform.chat_service.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.chat_service.dto.RelationShipSearchRequest;
import net.chess_platform.chat_service.dto.RelationshipDto;
import net.chess_platform.chat_service.service.RelationShipService;
import net.chess_platform.common.security.CurrentUser;

@RestController
@RequestMapping("/api/relationships")
public class RelationshipController {

    private RelationShipService relationShipService;

    public RelationshipController(RelationShipService relationShipService) {
        this.relationShipService = relationShipService;
    }

    @PostMapping("/search")
    public RelationshipDto search(@RequestBody RelationShipSearchRequest request, CurrentUser currentUser) {
        return relationShipService.getRelationship(request.ids(), currentUser);
    }
}
