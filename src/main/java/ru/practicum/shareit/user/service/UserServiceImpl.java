package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

//Сервиса для работы с пользователями
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Создание нового пользователя c проверкой полей
    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new RuntimeException("Name is required");
        }

        User user = UserMapper.toUser(userDto);
        User savedUser = userRepository.save(user);
        return UserMapper.toUserDto(savedUser);
    }

    //Обновление пользователя
    @Override
    public UserDto update(Long id, UserDto userDto) {
        //Проверка уникальности email
        if (userDto.getEmail() != null) {
            userRepository.findAll().stream()
                    .filter(u -> !u.getId().equals(id))
                    .filter(u -> userDto.getEmail().equals(u.getEmail()))
                    .findAny()
                    .ifPresent(u -> {
                        throw new RuntimeException("Email already exists");
                    });
        }

        //Проверка на существование пользователя
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            existingUser.setEmail(userDto.getEmail());
        }

        User updatedUser = userRepository.update(existingUser);
        return UserMapper.toUserDto(updatedUser);
    }

    //Получение пользователя по ID
    @Override
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.toUserDto(user);
    }

    //Получение списка всех пользователей
    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    //Удаление по ID
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
