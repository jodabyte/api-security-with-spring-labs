package de.jodabyte.apisecurity.bopla;

import de.jodabyte.apisecurity.bopla.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private final Set<User> users = new HashSet<>();

    public User createUser(User user) {
        user.setUuid(UUID.randomUUID().toString());
        user.setCreatedAt(LocalDate.now());
        user.setActive(true);
        this.users.add(user);
        return user;
    }

    public User getUser(String id) {
        return this.users.stream()
                .filter(user -> user.getUuid().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
