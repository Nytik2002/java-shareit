package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//Репозиторий для работы с пользователями
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}