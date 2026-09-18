package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

//Реализация сервиса для работы с пользователями
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    //Создание пользователя
    @Override
    public UserDto create(UserDto userDto) {
        validateEmailAvailable(userDto.getEmail());

        User user = UserMapper.toUser(userDto);
        User savedUser = userRepository.save(user);

        return UserMapper.toUserDto(savedUser);
    }

    //Обновление пользователя
    @Override
    public UserDto update(Long id, UserDto userDto) {
        User existingUser = getUserOrThrow(id);

        if (userDto.getEmail() != null) {
            validateEmailForUpdate(id, userDto.getEmail());
            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        User updatedUser = userRepository.save(existingUser);

        return UserMapper.toUserDto(updatedUser);
    }

    //Получение пользователя по ID
    @Override
    public UserDto getById(Long id) {
        User user = getUserOrThrow(id);

        return UserMapper.toUserDto(user);
    }

    //Получение всех пользователей
    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    //Удаление пользователя
    @Override
    public void delete(Long id) {
        validateUserExists(id);

        userRepository.deleteById(id);
    }

    //Получение пользователя или ошибка, если пользователь не найден
    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    //Проверка доступности email при создании пользователя
    private void validateEmailAvailable(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Email already exists");
        }
    }

    //Проверка email при обновлении пользователя
    private void validateEmailForUpdate(Long id, String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    if (!user.getId().equals(id)) {
                        throw new ConflictException("Email already exists");
                    }
                });
    }

    //Проверка существования пользователя перед удалением
    private void validateUserExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found");
        }
    }
}