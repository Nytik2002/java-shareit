package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
    }

    private User createUser(String name, String email) {
        User user = User.builder()
                .name(name)
                .email(email)
                .build();
        return userRepository.save(user);
    }

    @Test
    @DisplayName("Создание пользователя")
    void save_ShouldReturnUserWithId() {
        User result = createUser("John Doe", "john@example.com");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Поиск пользователя по ID")
    void findById_ShouldReturnUser() {
        User saved = createUser("John Doe", "john@example.com");

        Optional<User> result = userRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Поиск по несуществующему ID")
    void findById_NotFound_ShouldReturnEmpty() {
        Optional<User> result = userRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Обновление пользователя")
    void update_ShouldUpdateUser() {
        User saved = createUser("John Doe", "john@example.com");
        saved.setName("John Updated");
        saved.setEmail("updated@example.com");

        User result = userRepository.update(saved);

        assertThat(result.getName()).isEqualTo("John Updated");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteById_ShouldDeleteUser() {
        User saved = createUser("John Doe", "john@example.com");

        userRepository.deleteById(saved.getId());

        Optional<User> result = userRepository.findById(saved.getId());
        assertThat(result).isEmpty();
    }
}
