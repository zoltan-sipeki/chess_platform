package net.chess_platform.user_service.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.common.dto.user.UserDto;
import net.chess_platform.user_service.dto.UserSearchResultDto;
import net.chess_platform.user_service.mapper.UserMapper;
import net.chess_platform.user_service.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final UserMapper mapper;

    public UserController(UserService userService, UserMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping
    public UserSearchResultDto getUsersByPrefix(@RequestParam String startsWith,
            @PageableDefault(size = 5, sort = "displayName", direction = Direction.ASC) Pageable pageable) {
        return userService.findByDisplayNamePrefix(startsWith, pageable);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable UUID id, @RequestBody UserDto entity) {
        var user = mapper.toModel(entity);
        user.setId(id);
        userService.update(user);
        return mapper.toDto(user);
    }
}
