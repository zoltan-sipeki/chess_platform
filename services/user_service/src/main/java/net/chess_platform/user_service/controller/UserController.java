package net.chess_platform.user_service.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.chess_platform.common.dto.user.UserDto;
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

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable UUID id, @RequestBody UserDto entity) {
        var user = mapper.toModel(entity);
        user.setId(id);
        userService.update(user);
        return mapper.toDto(user);
    }
}
