package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Set<String> emails = new HashSet<>();

    public User save(User user) {
        if (user.getEmail() != null && emails.contains(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }

        users.put(user.getId(), user);
        if (user.getEmail() != null) {
            emails.add(user.getEmail());
        }
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User update(User user) {
        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }

        String oldEmail = existingUser.getEmail();
        String newEmail = user.getEmail();

        if (newEmail != null && !newEmail.equals(oldEmail)) {
            if (emails.contains(newEmail)) {
                throw new RuntimeException("Email already exists");
            }
            if (oldEmail != null) {
                emails.remove(oldEmail);
            }
            emails.add(newEmail);
            existingUser.setEmail(newEmail);
        }

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        users.put(existingUser.getId(), existingUser);
        return existingUser;
    }

    public void deleteById(Long id) {
        User user = users.remove(id);
        if (user != null && user.getEmail() != null) {
            emails.remove(user.getEmail());
        }
    }

    public boolean existsById(Long id) {
        return users.containsKey(id);
    }
}