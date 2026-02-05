package de.jodabyte.apisecurity.bopla;

import de.jodabyte.apisecurity.bopla.model.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;

    public UserController(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE')")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateDto dto) {
        User createdUser = this.userService.createUser(this.userMapper.map(dto));
        return ResponseEntity.ok(this.userMapper.map(createdUser));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> getUserViewInternal(@PathVariable String id) {
        User user = this.userService.getUser(id);
        return ResponseEntity.ok(this.userMapper.map(user));
    }

    @GetMapping("/public/{id}")
    @PreAuthorize("hasRole('PUBLIC')")
    public ResponseEntity<UserPublicDto> getUserViewPublic(@PathVariable String id) {
        User user = this.userService.getUser(id);
        return ResponseEntity.ok(this.userMapper.mapToPublic(user));
    }
}
