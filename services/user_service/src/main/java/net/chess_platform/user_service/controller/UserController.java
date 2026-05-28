package net.chess_platform.user_service.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.common.security.CurrentUser;
import net.chess_platform.user_service.dto.UserDto;
import net.chess_platform.user_service.dto.UserSearchResultDto;
import net.chess_platform.user_service.dto.UserUpdateDto;
import net.chess_platform.user_service.model.User;
import net.chess_platform.user_service.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/me")
    public UserDto updateCurrentUser(@RequestBody UserUpdateDto dto, CurrentUser currentUser) {
        var update = new User.Update();
        update.setId(currentUser.id());
        update.setDisplayName(dto.displayName());

        return userService.update(update, currentUser);
    }

    @GetMapping("/me")
    public UserDto getCurrentUser(CurrentUser currentUser) {
        return userService.findById(currentUser.id());
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @GetMapping
    public UserSearchResultDto getUsersByPrefix(@RequestParam String startsWith,
            @PageableDefault(size = 5, sort = "displayName", direction = Direction.ASC) Pageable pageable) {
        return userService.findByDisplayNamePrefix(startsWith, pageable);
    }
}
